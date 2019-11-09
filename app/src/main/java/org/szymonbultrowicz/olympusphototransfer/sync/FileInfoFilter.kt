package org.szymonbultrowicz.olympusphototransfer.sync

import org.szymonbultrowicz.olympusphototransfer.client.FileInfo
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*

class FileInfoFilter {
    
    companion object {
        val PatternPrefix = "glob:"

        fun isFileEligible(f: FileInfo, c: Criteria): Boolean {
            val fileDate = f.humanDate
            val fileName = f.name
            val fromRespected = c.fromDateCondition?.let { d -> fileDate.isAfter(d) || fileDate.isEqual(d) } ?: true
            val untilRespected = c.untilDateCondition?.let { d -> fileDate.isBefore(d) || fileDate.isEqual(d) } ?: true
            val nameRespected = c.fileNameConditions?.let { patterns -> fileNameConforms(fileName, patterns) } ?: true
            return fromRespected && untilRespected && nameRespected
        }

        private fun fileNameConforms(fileName: String, patterns: Sequence<String>): Boolean {
            val file = Paths.get(fileName.toUpperCase(Locale.getDefault()))
            val matchers = patterns.map { p ->
                FileSystems.getDefault().getPathMatcher(
                    PatternPrefix + p.toUpperCase(Locale.getDefault())
                )
            }
            return matchers.any { it.matches(file) }
        }
    }

    data class Criteria(
        val fromDateCondition: LocalDate?,
        val untilDateCondition: LocalDate?,
        val fileNameConditions: Sequence<String>?
    ) {

        companion object {
            val Bypass = Criteria()
        }
    }
}