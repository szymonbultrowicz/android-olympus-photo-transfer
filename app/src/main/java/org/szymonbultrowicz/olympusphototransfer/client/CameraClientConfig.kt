package org.szymonbultrowicz.olympusphototransfer.client

import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneId

data class CameraClientConfig(
    /**
     * Protocol to be used to contact the server.
     */
    val serverProtocol: String,

    /**
     * Name or IP address of the server.
     */
    val serverName: String,

    /**
     * Port of the http service provided by the server.
     */
    val serverPort: Int,

    /**
     * Relative URL used to contact the server.
     */
    val serverBaseUrl: String,

    /*
     * Regex used to identify files from the server's response
     * Sample: wlansd[17]="/DCIM/100OLYMP,P7290009.JPG,278023,0,18173,42481";
     */
    val fileRegex: String,

    /*
     * Flag to preserve the creation date for each file, as provided by the server
     */
    val preserveCreationDate: Boolean,

    /**
     * URL translator, used only for testing purposes
     */
    val urlTranslator: ((URL) -> URL)? = null,

    /**
     * Forced timezone (for tests mainly)
     */
    val forcedTimezone: ZoneId? = null
) {
    fun fileUrl(relativeUrl: String) =
        run {
            val r = URL(serverProtocol, serverName, serverPort, relativeUrl)
            (urlTranslator ?: { i -> i })(r)
        }

    /**
     * Zone offset to be used to interpret dates coming from the camera (which apparently has no information
     * about timezones).
     * The assumption: the timezone used to set up the time of the camera must be the same timezone on which
     * this application is executed.
     */
    val zoneOffset: ZoneId = forcedTimezone ?: OffsetDateTime.now().offset
}