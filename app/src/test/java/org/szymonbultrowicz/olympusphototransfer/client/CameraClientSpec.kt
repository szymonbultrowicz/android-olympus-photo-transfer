package org.szymonbultrowicz.olympusphototransfer.client

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.szymonbultrowicz.olympusphototransfer.TestHelper
import java.io.File
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

    @Test
    fun cameraServerClient_shouldCorrectlyListRemoteFilesWhenManyRemoteFilesFromOmdEM10() {
        val cc = CameraClient(
            generateClientCameraConfig(
                "01-root-em10-onefolder.html",
                specialMappingUrlTranslator(
                    "01-root-em10-onefolder.html",
                    "0001-em10-many-files.html"
                )
            )
        )
        assertEquals(135, cc.listFiles().size)
    }

    @Test
    fun cameraServerClient_shouldCorrectlyListRemoteFilesAndDownloadWhenHavingOneRemoteFileFromOmdEM10() {
        val cc = CameraClient(
            generateClientCameraConfig(
                "01-root-em10-onefolder.html",
                specialMappingUrlTranslator(
                    "01-root-em10-onefolder.html",
                    "0002-em10-downloadable-file.html"
                )
            )
        )

        // wlansd[0]="/DCIM/100OLYMP/,OR.ORF,15441739,0,18229,43541";
        val remoteFiles = cc.listFiles()
        assertEquals(1, remoteFiles.size)
        assertEquals("100OLYMP", remoteFiles[0].folder)
        assertEquals("OR.ORF", remoteFiles[0].name)
        assertEquals(15441739L, remoteFiles[0].size)
        assertEquals(18229, remoteFiles[0].date)
        assertEquals(43541, remoteFiles[0].time)
        assertNotNull(remoteFiles[0].thumbnailUrl)

        val outputDirectory = TestHelper.createTmpDir("output")

        val downloaded = cc.downloadFile(remoteFiles[0], outputDirectory)
        assertNotNull(downloaded)

        val downloadedFileToCheck = File(File(outputDirectory, "100OLYMP"), "OR.ORF")

        assertEquals(true, downloadedFileToCheck.exists())

        downloadedFileToCheck.deleteOnExit()
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