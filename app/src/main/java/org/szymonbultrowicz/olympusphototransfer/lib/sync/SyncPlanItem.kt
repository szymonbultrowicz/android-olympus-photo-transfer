package org.szymonbultrowicz.olympusphototransfer.lib.sync

import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo

data class SyncPlanItem(
    val fileInfo: FileInfo,
    val index: Index,
    val downloadStatus: DownloadedStatus
) {

    enum class DownloadedStatus {
        Downloaded,
        OnlyLocal,
        OnlyRemote,
        PartiallyDownloaded,
        Unknown,
    }

    data class Index(
        val i: Int,
        val total: Int
    ) {
        val percentage: Float = i.toFloat() / total
        val percentageAsStr: String = "${ (percentage * 100).toInt() }%"
    }

    companion object {

        fun from(fileInfo: FileInfo, index: Index, local: Map<String, FileInfo>, remote: Map<String, FileInfo>): SyncPlanItem {
            val status = isDownloaded(fileInfo, local, remote)
            return SyncPlanItem(fileInfo, index, status)
        }

        private fun isDownloaded(fileInfo: FileInfo, localFiles: Map<String, FileInfo>, remoteFiles: Map<String, FileInfo>): DownloadedStatus {
            val localSize = localFiles[fileInfo.getFileId()]?.size
            val remoteSize = remoteFiles[fileInfo.getFileId()]?.size
            if (localSize != null && remoteSize !== null) {
                return if (localSize != remoteSize) DownloadedStatus.PartiallyDownloaded else DownloadedStatus.Downloaded
            }
            if (localSize != null) {
                return DownloadedStatus.OnlyLocal
            }
            if (remoteSize != null) {
                return DownloadedStatus.OnlyRemote
            }
            return DownloadedStatus.Unknown
        }

    }
}
