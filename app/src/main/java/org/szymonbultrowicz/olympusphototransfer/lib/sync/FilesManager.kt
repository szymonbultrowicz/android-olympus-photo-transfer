package org.szymonbultrowicz.olympusphototransfer.lib.sync

import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import java.io.File
import java.util.logging.Logger

/**
 * Manages the file synchronization between camera and local filesystem.
 * @param api instance of [[CameraClient]] to be used to contact the camera
 * @param config
 */
class FilesManager(
    private val api: CameraClient,
    private val config: Config
) {
    /**
     * List files that are in the local filesystem.
     *
     * @return a list of [[FileInfo]]
     */
    fun listLocalFiles(): Sequence<FileInfo> {
        val directories = if (!config.outputDir.isDirectory)
            emptyList<File>()
        else
            config.outputDir.listFiles(DirectoriesFilter)?.toList() ?: emptyList()

        return directories.flatMap {
            directory ->
            val files = directory.listFiles()
            val filesAndSizes =
                files?.map { file -> FileInfo (directory.name, file.name, file.length()) }
                    ?: emptyList()
            filesAndSizes
        }.asSequence()
    }

    /**
     * List files that are in the remote filesystem (camera).
     *
     * @return a list of [[FileInfo]]
     */
    fun listRemoteFiles(): Sequence<FileInfo> {
        val files = api.listFiles()
        return files.filter { FileInfoFilter.isFileEligible(it, config.mediaFilter) }
            .asSequence()
    }

    /**
     * Prepare a plan to synchronize remote files with local files.
     *
     * @return sequence of [[SyncPlanItem]] to proceed with the synchronization
     */
    fun syncPlan(): Sequence<SyncPlanItem> {
        fun toMap(s: Sequence<FileInfo>) = s.map { i -> Pair(i.getFileId(), i) }.toMap()

        val remoteFiles = listRemoteFiles()
        val localFiles = listLocalFiles()
        val remoteFilesMap = toMap(remoteFiles)
        val localFilesMap = toMap(localFiles)

        return remoteFiles.withIndex().map { (index, fileInfo) ->
            SyncPlanItem.from(
                fileInfo,
                SyncPlanItem.Index(index, remoteFiles.toList().size),
                localFilesMap,
                remoteFilesMap
            )
        }
    }

    /**
     * Synchronize remote files with local files.
     * Synchronization is one-way (remote to local).
     *
     * @return result of the synchronization
     */
    fun sync(): Sequence<File?>
    {
        return syncPlan().map { syncPlanItem ->
            logger.info("Downloading ${syncPlanItem.index.i + 1} / ${syncPlanItem.index.total}...")
            syncFile(syncPlanItem)
        }
    }

    /**
     * Synchronize a single file based on the synchronization plan item of
     * it (local status, remote status, file info, etc.).
     *
     * @param syncPlanItem item in the synchronization plan
     * @return the local result of the synchronization
     */
    fun syncFile(syncPlanItem: SyncPlanItem): File? {
        return when (syncPlanItem.downloadStatus) {
            SyncPlanItem.DownloadedStatus.Downloaded,
            SyncPlanItem.DownloadedStatus.OnlyLocal -> {
                logger.fine("Skipping file ${syncPlanItem.fileInfo} as it's been already downloaded")
                null
            }
            else -> {
                logger.fine("Downloading file ${syncPlanItem.fileInfo} to ${config.outputDir} (previous status ${syncPlanItem.downloadStatus})")
                api.downloadFile(syncPlanItem.fileInfo, config.outputDir)
            }
        }
    }

    /**
     * Tell if the camera is reachable.
     * @return true if the camera is reachable, false otherwise
     */
    fun isRemoteConnected(): Boolean = api.isConnected()

    companion object FilesManager {

        private val logger = Logger.getLogger(FilesManager::class.toString())

        val DirectoriesFilter = { f: File -> f.isDirectory }

        data class Config(
            val outputDir: File,
            val mediaFilter: FileInfoFilter.Criteria = FileInfoFilter.Criteria.Bypass
        )
    }
}