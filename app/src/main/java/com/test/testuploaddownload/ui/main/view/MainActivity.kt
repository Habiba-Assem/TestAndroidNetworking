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
import com.androidnetworking.interfaces.DownloadProgressListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.test.testuploaddownload.commo.UploadService
import com.test.testuploaddownload.common.Constant
import com.test.testuploaddownload.databinding.ActivityMainBinding
import com.test.testuploaddownload.ui.main.viewModel.MainActivityViewModel
import com.test.testuploaddownload.utilities.extension.observe
import org.json.JSONObject
import java.io.File


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
        AndroidNetworking.initialize(applicationContext)
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
                    result.data?.data?.apply {
                        val file = File(result?.data?.data?.toString()?: "")
                        println(result?.data?.data.toString())
                        println(file.name)
                        uploadService.upload(file, Constant.Api.UPLOAD_ATTACHMENT_PATH)
                    }
                }
            }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"//Constant.PDF_TYPE
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
            .setDownloadProgressListener(object : DownloadProgressListener {
                override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
                    // do anything with progress
                }
            })
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: ANError?) {
                    Toast.makeText(applicationContext, "Download failed: ${error?.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onFinishUploading(fileId: Int, fileName: String) {
        viewModel.fileName = fileName
        viewModel.fileID = fileId
        binding.uploadTxt.text = "Uploaded file: $fileName"
        binding.uploadTxt.visibility= View.VISIBLE
    }
}
