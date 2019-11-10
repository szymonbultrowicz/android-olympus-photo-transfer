package org.szymonbultrowicz.olympusphototransfer.lib.sync

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.szymonbultrowicz.olympusphototransfer.TestHelper
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilesManagerSpec {

    private val olympusFolder = "100OLYMP"

    private var tmpDir = createTempDir()

    @AfterEach
    fun teadDown() {
        tmpDir.delete()
    }

    @Test
    fun filesManager_shouldCorrectlyListLocallyAlreadyDownloadedFiles() {
        // Simulate downloads local directory and some photos
        TestHelper.touchFile(File(tmpDir, olympusFolder), "photo1.jpg")
        TestHelper.touchFile(File(tmpDir, olympusFolder), "photo2.jpg")

        // Simulate camera
        val cameraClientMock = mock(CameraClient::class.java)

        val fm = FilesManager(cameraClientMock, FilesManager.FilesManager.Config(tmpDir))

        assertEquals(
            setOf(FileInfo(olympusFolder, "photo1.jpg", 0L), FileInfo(olympusFolder, "photo2.jpg", 0L)),
            fm.listLocalFiles().toSet()
        )
    }

    @Test
    fun filesManager_shouldCorrectlySynchronizeARemoteFileInTheCameraThatWasNotYetDownloadedLocally() {
        // Simulate local download directory and photo1.jpg (but not photo2.jpg)
        val photo1 = TestHelper.touchFile(File(tmpDir, olympusFolder), "photo1.jpg")
        val photo2 = File(File(tmpDir, olympusFolder), "photo2.jpg") // not present (not touched)

        assertTrue(photo1.exists())
        assertFalse(photo2.exists())

        // Simulate camera reporting that photo2.jpg is available
        val cameraClientMock = mock(CameraClient::class.java)
        val fiPhoto2 = FileInfo(olympusFolder, "photo2.jpg", 100L)
        val remoteFilesMock = listOf(fiPhoto2)
        `when`(cameraClientMock.listFiles()).thenReturn(remoteFilesMock)
        `when`(cameraClientMock.downloadFile(fiPhoto2, tmpDir)).
            thenReturn(TestHelper.touchFile(File(tmpDir, olympusFolder), "photo2.jpg"))

        // The manager should download the file photo2.jpg
        val fm = FilesManager(cameraClientMock, FilesManager.FilesManager.Config(tmpDir))
        assertEquals(listOf(photo2), fm.sync().toList())

        // There should be downloaded photo2.jpg and old photo1.jpg in local directory
        assertTrue(photo1.exists())
        assertTrue(photo2.exists())
    }

    @Test
    fun filesManager_shouldCorrectlySynchronizeARemoteFileInTheCameraThatHadBeenAlreadyDownloadedLocally() {
        // Simulate downloads local directory and photo1.jpg
        val photo1 = TestHelper.touchFile(File(tmpDir, olympusFolder), "photo1.jpg")

        assertTrue(photo1.exists())

        // Simulate camera telling that photo1.jpg is available
        val cameraClientMock = mock(CameraClient::class.java)
        val fiPhoto1 = FileInfo(olympusFolder, "photo1.jpg", 0L)
        val remoteFilesMock = listOf(fiPhoto1)
        `when`(cameraClientMock.listFiles()).thenReturn(remoteFilesMock)
        `when`(cameraClientMock.downloadFile(fiPhoto1, tmpDir)).
            thenReturn(TestHelper.touchFile(File(tmpDir, olympusFolder), "photo1.jpg"))

        // The manager should skip downloading file photo1.jpg
        val fm = FilesManager(cameraClientMock, FilesManager.FilesManager.Config(tmpDir))
        val syncResult = fm.sync()
        assertEquals(1, syncResult.toList().size)
        assertNull(syncResult.first())
    }

    @Test
    fun filesManager_shouldCorrectlyHandleAFailureWhenSynchronizingAFile() {
        // Simulate camera telling that photo1.jpg is available
        val cameraClientMock = mock(CameraClient::class.java)
        val fiPhoto1 = FileInfo(olympusFolder, "photo1.jpg", 100L)
        val remoteFilesMock = listOf(fiPhoto1)
        `when`(cameraClientMock.listFiles()).thenReturn(remoteFilesMock)
        `when`(cameraClientMock.downloadFile(fiPhoto1, tmpDir)).thenReturn(null)

        // The manager should download the file photo2.jpg
        val fm = FilesManager(cameraClientMock, FilesManager.FilesManager.Config(tmpDir))
        val syncResult = fm.sync()
        assertEquals(1, syncResult.toList().size)
        assertNull(syncResult.first())

    }

    @Test
    fun filesManager_shouldCorrectlyListWhatAreTheRemoteFiles() {
        // Simulate camera telling that photo1.jpg and photo2.jpg are available
        val cameraClientMock = mock(CameraClient::class.java)
        val remoteFilesMock = listOf(FileInfo(olympusFolder, "photo1.jpg", 100L), FileInfo(olympusFolder, "photo2.jpg", 100L))
        `when`(cameraClientMock.listFiles()).thenReturn(remoteFilesMock)

        // The manager should list both files
        val fm = FilesManager(cameraClientMock,
            FilesManager.FilesManager.Config(File ("output"))
        )
        assertEquals(remoteFilesMock, fm.listRemoteFiles().toList())
    }
}