package com.test.testuploaddownload.commo

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.test.testuploaddownload.common.Constant
import org.json.JSONObject
import java.io.File


class UploadService(
    private val context: Context,
    private val delegate: Delegate
) {


    fun upload(fileToUpload: File, url:String) {
        AndroidNetworking.upload(url)
            .addMultipartFile("file", fileToUpload)
            .addHeaders("x-qms-access-key", "5984B9EC-6411-485F-AC58-9BD2BD734D2A")
            .addHeaders(Constant.Api.SESSION_ID, Constant.Api.PREFIX_TOKEN + Constant.token)
            .setTag("uploadTest")
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener { _, _ ->
                // do anything with progress
            }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    // do anything with response
                    val fileName = response.optString("fileName", "") // Extract the file name from the "fileName" field
                    val bodyString = response.toString()
                    val responseObject = Gson().fromJson(bodyString,Response::class.java)
                    val itemId = responseObject?.item?.id ?: -1
                    delegate.onFinishUploading(itemId, fileName)
                    Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: ANError) {
                    println("Upload failed: ${error.message}")
                    delegate.onFinishUploading(-1, fileToUpload.name)
                    // handle error
                    Toast.makeText(context, "Upload failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    interface Delegate {
        fun onFinishUploading(fileId: Int, fileName: String)
    }


    data class Response (
        @SerializedName("item") val item : Item,
        @SerializedName("status") val status : Status
    )

    data class Status (
        @SerializedName("statusCode") val statusCode : Int,
        @SerializedName("reason") val reason : String
    )

    data class Item (
        @SerializedName("id") val id : Int
    )

}