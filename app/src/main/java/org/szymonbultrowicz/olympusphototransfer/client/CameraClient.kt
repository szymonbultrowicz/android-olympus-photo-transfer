package org.szymonbultrowicz.olympusphototransfer.client

import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.logging.Logger

class CameraClient(
    private val configuration: CameraClientConfig
) {

    private val logger = Logger.getLogger(javaClass.name)

    /**
     * Lists all remote files
     * @return the list of remote [[FileInfo]] with their attributes
     */
    fun listFiles(): List<FileInfo> {
        val rootUrl = baseDirFileUrl(configuration.serverBaseUrl)
        val rootHtmlLines = httpGetAsString(rootUrl)

        logger.info("Html root begin ($rootUrl)")
        rootHtmlLines.forEach { line -> logger.info(line) }
        logger.info("Html root end")

        val remoteDirs = dirsFromRootHtml(rootHtmlLines)

        remoteDirs.forEach { folder -> logger.info("Detected remote folder: $folder") }

        val files = remoteDirs.flatMap { dir ->
            val dirUrl = baseDirFileUrl(configuration.serverBaseUrl, dir)
            val dirHtmlLines = httpGetAsString(dirUrl)
            logger.info("Html for directory begin ($dirUrl / $dir)")
            dirHtmlLines.forEach { line -> logger.info(line) }
            logger.info("Html for directory end\n")
            filesFromDirHtml(dirHtmlLines, dir)
        }

        files.forEach { file ->
            logger.info("Detected remote file: $file (created on ${file.humanDateTime})")
        }

        return files
    }

    private fun setDateTime(destinationFile: File, dateTime: ZonedDateTime): Unit {
        if (configuration.preserveCreationDate) {
            val epochSecs = dateTime.toEpochSecond()
            val success = destinationFile.setLastModified(epochSecs * 1000)
            if (!success) {
                logger.warning("Could not setup file date for: ${destinationFile.name}")
            }
        }
    }

    /**
     * Downloads a specific file
     *
     * @param file the remote file information
     * @param localTargetDirectory the target local directory
     * @return Downloaded local file
     */
    fun downloadFile(file: FileInfo, localTargetDirectory: File): File? {
        val urlSourceFile = configuration.fileUrl(baseDirFileUrl(configuration.serverBaseUrl, file.folder, file.name))
        val inputStream = urlSourceFile.openStream()
        try {
            val channel = Channels.newChannel(inputStream)
            val localDirectory = File (localTargetDirectory, file.folder)
//            Directories.mkdirs(localDirectory)
            val destinationFile = File (localDirectory, file.name)
            val outputStream = FileOutputStream (destinationFile)
            outputStream.channel.transferFrom(channel, 0, Long.MAX_VALUE)
            setDateTime(destinationFile, file.humanDateTime.atZone(configuration.zoneOffset))
            return destinationFile.absoluteFile
        } catch (e: Exception) {
            return null
        } finally {
            inputStream.close()
        }
    }

    /**
     * Tells if the camera is reachable
     * @return true if connected
     */
    fun isConnected(): Boolean {
        val rootUrl = baseDirFileUrl(configuration.serverBaseUrl)
        return try {
            httpGet(rootUrl, IsConnectedTimeout, IsConnectedTimeout)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves the URL of the thumbnail of a given media file
     * @param remoteDir directory
     * @param remoteFile file
     * @return the URL pointing to the thumbnail image
     */
    private fun thumbnailFileUrl(remoteDir: String, remoteFile: String): URL {
        val fileUrlPart = baseDirFileUrl(configuration.serverBaseUrl, remoteDir, remoteFile)
        val relativeUrl = "/get_thumbnail.cgi?DIR=$fileUrlPart"
        return configuration.fileUrl(relativeUrl)
    }

    /**
     * Tries to shutdown the camera
     * @return the response from the server
     */
    fun shutDown(): List<String> {
        val reply = httpGetAsString("/exec_pwoff.cgi")
        logger.info("Shutdown complete: $reply")
        return reply
    }

    /**
     * Does a GET to the given relative url (transforms into text)
     * @param relativeUrl
     * @return the collection of lines result of the query
     */
    private fun httpGetAsString(relativeUrl: String): List<String> {
        logger.info("Querying URL $relativeUrl...")
        val str = String(httpGet(relativeUrl), StandardCharsets.ISO_8859_1)
        return str.split(NewLineSplit)
    }

    /**
     * Does a GET to the given relative url
     * @param relativeUrl relative url to perform the verb against (base url is taken from configuration)
     * @param readTimeout read timeout as per [[java.net.URLConnection]]
     * @param connectTimeout connect timeout as per [[java.net.URLConnection]]
     * @return the reply result of the query
     */
    private fun httpGet(relativeUrl: String, readTimeout: Int = ReadTimeoutMs, connectTimeout: Int = ConnectTimeoutMs): ByteArray {
        val url = configuration.fileUrl(relativeUrl)
        val connection = url.openConnection()
        connection.connectTimeout = connectTimeout
        connection.readTimeout = readTimeout
        val inputStream = connection.getInputStream()
        return inputStream.use { stream ->
            stream.readBytes()
        }
    }

    /**
     * Gets the collection of directories from HTML at root level
     * @param rootHtmlLines text lines as obtained from a GET at root level
     * @return the collection of remote directories
     */
    private fun dirsFromRootHtml(rootHtmlLines: List<String>): List<String> {
        val fileRegex = configuration.fileRegex.toRegex()
        val folderNames = rootHtmlLines.flatMap { htmlLineToBeParsed ->
            when {
                fileRegex.matches(htmlLineToBeParsed) -> {
                    val (folderName) = fileRegex.findAll(htmlLineToBeParsed)
                        .map { group -> group.value }
                        .toList()
                    return listOf(folderName)
                }
                else -> emptyList<String>()
            }
        }

        return folderNames.distinct()
    }

    /**
     * Gets the collection of files from HTML at directory level
     * @param dirHtmlLines text lines as obtained from a GET at dir level
     * @param fileDir directory that is being targetted
     * @return the collection of [[FileInfo]] inside such directory
     */
    private fun filesFromDirHtml(dirHtmlLines: List<String>, fileDir: String): List<FileInfo> {
        val fileRegex = configuration.fileRegex.toRegex()
        val fileIdsAndSize = dirHtmlLines.flatMap { htmlLineToBeParsed ->
            when {
                fileRegex.matches(htmlLineToBeParsed) -> {
                    val (fileName, fileSizeBytes, _, date, time) =
                        fileRegex.findAll(htmlLineToBeParsed)
                            .map { group -> group.value }
                            .toList()

                    val thumbnail = thumbnailFileUrl(fileDir, fileName)
                    return listOf(FileInfo(
                        fileDir,
                        fileName,
                        fileSizeBytes.toLong(),
                        date.toInt(),
                        time.toInt(),
                        thumbnail
                    ))
                }
                else -> emptyList<FileInfo>()
            }
        }

        return fileIdsAndSize.distinct()
    }

    /**
     * Builds a relative URL from the arguments
     * @param base base url
     * @param dir directory
     * @param file file
     * @return the resulting url
     */
    private fun baseDirFileUrl(base: String?, dir: String? = null, file: String? = null): String {
        val filePart = if(file != null) UrlSeparator + file else "" // "" or /file
        val dirFilePart = if(dir != null) UrlSeparator + dir + filePart else "" // "" or /dir + <file>
        return if(base != null) base + dirFilePart else ""  // "" or /base + <dirfile>
    }

    companion object CameraClient {
        const val UrlSeparator = "/"
        const val IsConnectedTimeout = 1000 // TODO make configurable
        const val ConnectTimeoutMs = 20000 // TODO make configurable
        const val ReadTimeoutMs = 20000 // TODO make configurable
        const val NewLineSplit = "\\r?\\n"
//        val LocalZoneOffset = OffsetDateTime.now().getOffset()
    }
}