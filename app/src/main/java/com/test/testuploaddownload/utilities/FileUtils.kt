package com.test.testuploaddownload.utilities
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    @Throws(IOException::class)
    fun copyFileToTemp(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Failed to open input stream for URI: $uri")

        val originalFileName = getFileName(context, uri)
        val tempFile = createTempFileWithOriginalName(originalFileName)

        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        return tempFile
    }

    private fun getFileName(context: Context, uri: Uri): String {
        val filename: String
        val cursor = context.contentResolver?.query(uri,null,null,null,null)
        if(cursor == null) {
            filename = uri.path ?: ""
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            filename = cursor.getString(idx)
            cursor.close()
        }

        val name = filename.substring(0,filename.lastIndexOf("."))
        val extension = filename.substring(filename.lastIndexOf(".")+1)
        return "$name.$extension"
    }

    private fun createTempFileWithOriginalName(originalFileName: String): File {
        val tempDir = File.createTempFile("temp", null).parentFile
        return File(tempDir, originalFileName)
    }
}
