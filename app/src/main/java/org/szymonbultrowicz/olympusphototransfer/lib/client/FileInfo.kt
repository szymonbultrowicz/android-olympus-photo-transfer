package org.szymonbultrowicz.olympusphototransfer.lib.client

import java.net.URL
import java.time.LocalDateTime
import java.util.*

data class FileInfo (
    val folder: String,
    override val name: String,
    val size: Long,
    override val dateTaken: LocalDateTime,
    override val thumbnailUrl: URL? = null // if local, no thumbnail will be available
) : BasePhotoInfo {
    val baseFileName = name.substringBeforeLast('.')
    val extension = name.substringAfterLast('.').toLowerCase(Locale.getDefault())
    val mediaType = when (extension) {
        "jpg" -> "image/jpeg"
        "orf" -> "image/x-olympus-orf"
        else -> "application/octet-stream"
    }
}
