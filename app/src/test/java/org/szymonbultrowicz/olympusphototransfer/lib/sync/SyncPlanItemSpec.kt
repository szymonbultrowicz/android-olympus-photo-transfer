package org.szymonbultrowicz.olympusphototransfer.lib.sync

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo

class SyncPlanItemSpec {

    private val olympusFolder = "100OLYMP"
    private val aFileInfo = FileInfo(olympusFolder, "photo.jpg", 10L)

    @Test
    fun syncPlainItem_shouldCorrectlySetTheDownloadedStatusIfAlreadyDownloaded() {
        val localFiles = mapOf(Pair(aFileInfo.getFileId(), aFileInfo))
        val remoteFiles = mapOf(Pair(aFileInfo.getFileId(), aFileInfo))

        // The manager should tell the file's already synchronized/downloaded
        val syncPlanItem = SyncPlanItem.from(aFileInfo,
            SyncPlanItem.Index(0, 10), localFiles, remoteFiles)
        assertEquals(SyncPlanItem.DownloadedStatus.Downloaded, syncPlanItem.downloadStatus)
    }

    @Test
    fun syncPlainItem_shouldCorrectlySetTheDownloadedStatusIfNotDownloadedYet() {
        // File downloaded already but it has different length than the local file
        val fi5 = aFileInfo.copy(size = 5L)
        val fi10 = aFileInfo.copy(size = 10L)
        val localFiles = mapOf(Pair(fi5.getFileId(), fi5))
        val remoteFiles = mapOf(Pair(fi10.getFileId(), fi10))

        val syncPlanItem = SyncPlanItem.from(fi10, SyncPlanItem.Index(0, 10), localFiles, remoteFiles)
        assertEquals(SyncPlanItem.DownloadedStatus.PartiallyDownloaded, syncPlanItem.downloadStatus)
    }

    @Test
    fun syncPlainItem_shouldCorrectlyTellIfAFileWasNotDownloaded() {
        // Simulate camera telling there is one file to be downloaded
        val localFiles = emptyMap<String, FileInfo>()
        val remoteFiles = mapOf(Pair(aFileInfo.getFileId(), aFileInfo))

        // The manager should tell the file has not been donwloaded yet
        val syncPlanItem = SyncPlanItem.from(aFileInfo,
            SyncPlanItem.Index(0, 10), localFiles, remoteFiles)
        assertEquals(SyncPlanItem.DownloadedStatus.OnlyRemote, syncPlanItem.downloadStatus)
    }

    @Test
    fun syncPlainItem_shouldCorrectlyTellIfAFileIsPresentOnlyLocally() {
        // No files in camera, a file locally
        val remoteFiles = emptyMap<String, FileInfo>()
        val localFiles = mapOf(Pair(aFileInfo.getFileId(), aFileInfo))

        // The manager should tell the file is only locally
        val syncPlanItem = SyncPlanItem.from(aFileInfo,
            SyncPlanItem.Index(0, 10), localFiles, remoteFiles)
        assertEquals(SyncPlanItem.DownloadedStatus.OnlyLocal, syncPlanItem.downloadStatus)
    }
}