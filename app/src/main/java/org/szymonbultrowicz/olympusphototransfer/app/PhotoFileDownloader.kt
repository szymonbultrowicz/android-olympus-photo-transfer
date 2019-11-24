package org.szymonbultrowicz.olympusphototransfer.app

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import org.szymonbultrowicz.olympusphototransfer.lib.exceptions.PhotoDownloadException

class PhotoFileDownloader {

    suspend fun downloadFile(
        file: FileInfo,
        camera: CameraClient,
        contentResolver: ContentResolver
    ) = withContext(Dispatchers.IO) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, file.mediaType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/${file.folder}")
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw PhotoFileSaveException("Content resolver returned empty URI")

        try {
            contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    camera.downloadFile(file, outputStream)
                }
            }
            uri
        } catch (e: PhotoDownloadException) {
            contentResolver.delete(uri, null, null)
            throw e
        }
    }
}