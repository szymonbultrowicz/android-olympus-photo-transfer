package org.szymonbultrowicz.olympusphototransfer.sync

import org.szymonbultrowicz.olympusphototransfer.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.client.FileInfo
import java.io.File
import java.io.FileFilter
import java.util.logging.Logger

/**
 * Manages the file synchronization between camera and local filesystem.
 * @param api instance of [[CameraClient]] to be used to contact the camera
 * @param config
 */
class FilesManager(
    val api: CameraClient,
    val config: Config
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
            config.outputDir.listFiles(FilesManager.DirectoriesFilter)?.toList() ?: emptyList()

        directories.flatMap {
            directory ->
            val files = directory.listFiles()
            val filesAndSizes =
                files?.map { file -> FileInfo (directory.getName, file.getName, file.length) }
                    ?: emptyList()
            filesAndSizes
        }
    }

    /**
     * List files that are in the remote filesystem (camera).
     *
     * @return a list of [[FileInfo]]
     */
    fun listRemoteFiles(): Sequence<FileInfo> {
        val files = api.listFiles()
        val filteredFiles = files.filter(FileInfoFilter.isFileEligible(_, config.mediaFilter))
        filteredFiles
    }

    /**
     * Prepare a plan to synchronize remote files with local files.
     *
     * @return sequence of [[SyncPlanItem]] to proceed with the synchronization
     */
    fun syncPlan(): Sequence<SyncPlanItem> {
        fun toMap(s: Sequence<FileInfo>) = s.map(i =>(i.getFileId, i)).toMap

        val remoteFiles = listRemoteFiles()
        val localFiles = listLocalFiles()
        val remoteFilesMap = toMap(remoteFiles)
        val localFilesMap = toMap(localFiles)

        remoteFiles.zipWithIndex.map {
            case(
                fileInfo,
                index
            ) => SyncPlanItem(fileInfo, SyncPlanItem.Index(index, remoteFiles.length), localFilesMap, remoteFilesMap)
        }
    }

    /**
     * Synchronize remote files with local files.
     * Synchronization is one-way (remote to local).
     *
     * @return result of the synchronization
     */
    fun sync(): Sequence<Try[File]>
    {
        val syncPlanItems = syncPlan()
        syncPlanItems.map {
            case syncPlanItem @ SyncPlanItem(fileInfo, SyncPlanItem.Index(index, total), status) =>
            logger.info(s"Downloading ${index + 1} / ${total}...")
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
    fun syncFile(
        syncPlanItem: SyncPlanItem
    ): Try[File]
    {
        syncPlanItem.downloadStatus match {
            case i @ (SyncPlanItem.Downloaded | SyncPlanItem.OnlyLocal) =>
            logger.debug(s"Skipping file ${syncPlanItem.fileInfo} as it's been already downloaded")
            Failure(new AlreadyDownloadedException (syncPlanItem.fileInfo.name))
            case i @ (SyncPlanItem.OnlyRemote | SyncPlanItem.PartiallyDownloaded) =>
            logger.debug(
                s
                "Downloading file ${syncPlanItem.fileInfo} to ${config.outputDir} (previous status ${syncPlanItem.downloadStatus})"
            )
            api.downloadFile(syncPlanItem.fileInfo, config.outputDir)
        }
    }

    /**
     * Tell if the camera is reachable.
     * @return true if the camera is reachable, false otherwise
     */
    fun isRemoteConnected(): Boolean = api.isConnected

    companion object FilesManager {

        private val logger = Logger.getLogger(FilesManager.javaClass.name)

        val DirectoriesFilter = { f: File -> f.isDirectory }

        data class Config(
            val outputDir: File,
            val mediaFilter: FileInfoFilter.Criteria = FileInfoFilter.Criteria.Bypass
        )
    }
}