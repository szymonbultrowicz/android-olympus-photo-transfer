package org.szymonbultrowicz.olympusphototransfer.lib.client

import java.net.URL

data class PhotoInfo(
    val files: Set<FileInfo>
): BasePhotoInfo {
    init {
        require(files.isNotEmpty()) {
            "PhotoInfo files set must not be empty"
        }
    }

    override val name = files.first().baseFileName
    override val dateTaken = files.first().dateTaken
    override val thumbnailUrl: URL? = files.firstOrNull { it.thumbnailUrl !== null }?.thumbnailUrl

    val hasRaw = files.any { f -> f.extension == ORF_EXT }
    val hasJpg = files.any { f -> f.extension == JPG_EXT }

    companion object {
        const val ORF_EXT = "orf"
        const val JPG_EXT = "jpg"
    }
}