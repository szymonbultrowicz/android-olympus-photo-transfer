package org.szymonbultrowicz.olympusphototransfer.client

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URL
import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime

class CameraClientSpec {

    val ParisZone = ZoneId.of("Europe/Paris")
    val ADateTime = ZonedDateTime.of(2015, 9, 21, 21, 16, 21, 0, ParisZone)
    val ServerBaseUrl = "src/test/resources/org/szymonbultrowicz/olympusphototransfer/client/"

    val DefaultCameraClientConfig = CameraClientConfig(
        serverProtocol = "file",
        serverName = "localhost",
        serverBaseUrl = "/DCIM",
        serverPort = 0,
        fileRegex = """wlan.*=.*,(.*),(\d+),(\d+),(\d+),(\d+).*""",
        preserveCreationDate = true,
        urlTranslator = null,
        forcedTimezone = ParisZone // the app figures out the zone of the PC, which must be the same as the one in the camera
    )

    @Test
    fun cameraServerClient_shouldCorrectlyListRemoteFilesWhenEmptyFromOmdEM10() {
        val cc = CameraClient(
                generateClientCameraConfig(
                    "00-root-em10-nofolder.html",
                    specialMappingUrlTranslator("00-root-em10-nofolder.html", "0000-em10-no-files.html")
                )
            )
        assertEquals(emptyList<FileInfo>(), cc.listFiles())
    }

    fun generateClientCameraConfig(rootHtmlName: String, mapping: (URL) -> URL): CameraClientConfig {
        return DefaultCameraClientConfig.copy(
            serverBaseUrl = ServerBaseUrl + rootHtmlName,
            urlTranslator = mapping
        )
    }

    fun specialMappingUrlTranslator(rootHtmlName: String, folderHtmlName: String): (URL) -> URL {
        // Tricky translations to mock up responses of the camera
        fun transformRelativeUrl(file: String): String {
            return Paths.get(
                file
                    .replace("$rootHtmlName/100OLYMP", "100OLYMP/$folderHtmlName")
                    .replace("$folderHtmlName/", "photosample/")
            ).toAbsolutePath().toString()
        }

        return { url: URL ->
            URL(
                url.protocol,
                "",
                -1,
                transformRelativeUrl(url.file)
            )
        }
    }
}