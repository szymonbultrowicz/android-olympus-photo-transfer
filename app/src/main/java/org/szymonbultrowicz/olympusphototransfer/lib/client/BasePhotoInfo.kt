package org.szymonbultrowicz.olympusphototransfer.lib.client

import java.net.URL
import java.time.LocalDateTime

interface BasePhotoInfo {

    val name: String
    val dateTaken: LocalDateTime
    val thumbnailUrl: URL?

}