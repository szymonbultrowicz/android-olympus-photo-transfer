package org.szymonbultrowicz.olympusphototransfer

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class TestHelper {

    companion object {
        fun touchFile(parent: File, filename: String): File {
            val file = File(parent, filename)
            file.parentFile?.mkdirs()
            file.createNewFile()
            file.deleteOnExit()
            return file
        }

        fun createTmpFile(prefix: String, size: Long): File {
            val file = File.createTempFile(prefix, "tmp")
            file.deleteOnExit()
            Files.write(Paths.get(file.absolutePath), " ".repeat(size.toInt()).byteInputStream(StandardCharsets.UTF_8).readBytes())
            return file
        }

        fun createTmpDir(prefix: String): File {
            val path = Files.createTempDirectory("photosync-tmp")
            val file = path.toFile()
            file.deleteOnExit()
            return file
        }
    }
}