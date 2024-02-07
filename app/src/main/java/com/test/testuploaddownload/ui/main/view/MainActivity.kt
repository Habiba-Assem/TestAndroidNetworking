package com.test.testuploaddownload.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.jacksonandroidnetworking.JacksonParserFactory
import com.test.testuploaddownload.commo.UploadService
import com.test.testuploaddownload.common.Constant
import com.test.testuploaddownload.databinding.ActivityMainBinding
import com.test.testuploaddownload.ui.main.viewModel.MainActivityViewModel
import com.test.testuploaddownload.utilities.FileUtils
import com.test.testuploaddownload.utilities.extension.observe
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), UploadService.Delegate {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var uploadService: UploadService
    private lateinit var fileImportLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        subscribeUi()
    }

    private fun initUi() {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build()
        AndroidNetworking.initialize(applicationContext, okHttpClient)
        AndroidNetworking.setParserFactory(JacksonParserFactory())
        uploadService = UploadService(applicationContext, this)
        setUpLauncher()
    }

    private fun subscribeUi() {
        observe(binding.uploadBtn, ::pickFile)
        observe(binding.downloadBtn, ::downloadFile)
    }

    private fun setUpLauncher() {
        fileImportLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        val file = FileUtils.copyFileToTemp(applicationContext, uri)
                        println(file)
                        println(file.name)
                        uploadService.upload(file)
                    }
                }
            }

    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        fileImportLauncher.launch(intent)
    }


    private fun downloadFile() {
        val fileUrl = Constant.Api.getAttachmentUrl(viewModel.fileID)

        AndroidNetworking.download(fileUrl, Constant.DOWNLOADS_DIR, fileUrl)
            .setTag(Constant.Api.DOWNLOAD_TAG)
            .addHeaders("x-qms-access-key", "5984B9EC-6411-485F-AC58-9BD2BD734D2A")
            .addHeaders(Constant.Api.SESSION_ID, Constant.Api.PREFIX_TOKEN + Constant.token)
            .setPriority(Priority.MEDIUM)
            .build()
            .setDownloadProgressListener { _, _ -> enableLoading() }
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    binding.progressBar.visibility= View.GONE
                    Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: ANError?) {
                    Toast.makeText(applicationContext, "Download failed: ${error?.message}", Toast.LENGTH_SHORT).show()
                    println("Download failed: ${error?.message}")
                }
            })
    }

    override fun onFinishUploading(fileId: Int, fileName: String) {
        viewModel.fileName = fileName
        viewModel.fileID = fileId
        binding.uploadTxt.text = "Uploaded file: $fileName"
        binding.uploadTxt.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun enableLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }
}
