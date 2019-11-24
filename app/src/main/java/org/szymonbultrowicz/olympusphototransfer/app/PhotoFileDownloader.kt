package org.szymonbultrowicz.olympusphototransfer.app

import android.content.ContentResolver
import android.content.ContentValues
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import org.szymonbultrowicz.olympusphototransfer.lib.exceptions.PhotoDownloadException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class PhotoFileDownloader {

    val exifDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy:MM:dd hh:mm:ss", Locale.getDefault())

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
            if (file.extension == "jpg") {
                contentResolver.openFileDescriptor(uri, "rw")?.use {
                    // set Exif attribute so MediaStore.Images.Media.DATE_TAKEN will be set
                    val takenTimestamp = file.dateTaken.atZone(ZoneId.systemDefault()).toInstant()
                    ExifInterface(it.fileDescriptor)
                        .apply {
                            setAttribute(
                                ExifInterface.TAG_DATETIME_ORIGINAL,
                                exifDateFormat.format(Date.from(takenTimestamp))
                            )
                            saveAttributes()
                        }
                }
            }
            uri
        } catch (e: PhotoDownloadException) {
            contentResolver.delete(uri, null, null)
            throw e
        }
    }
}