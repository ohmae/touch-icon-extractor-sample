package net.mm2d.webclip.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.OkHttpClientHolder
import okhttp3.Request
import java.io.FileOutputStream

object Downloader {
    @Suppress("BlockingMethodInNonBlockingContext")
    fun download(context: Context, icon: Icon): Boolean {
        val result = OkHttpClientHolder.client.newCall(
            Request.Builder().url(icon.url).build()
        ).execute()
        if (!result.isSuccessful) return false
        val data = result.body?.byteStream()?.readBytes() ?: return false

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val fileName = Uri.parse(icon.url).let { uri ->
            (uri.host + uri.path).replace("/", "_")
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, icon.mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val item = context.contentResolver.insert(collection, values) ?: return false
        context.contentResolver.openFileDescriptor(item, "w", null)?.use {
            FileOutputStream(it.fileDescriptor).use { outputStream ->
                outputStream.write(data)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(item, values, null, null)
        }
        return true
    }
}
