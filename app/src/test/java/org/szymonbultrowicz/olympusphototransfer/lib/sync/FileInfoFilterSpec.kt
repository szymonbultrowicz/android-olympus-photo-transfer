package org.szymonbultrowicz.olympusphototransfer.lib.sync

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileInfoFilterSpec {

    private val Date2k0101Ticks = FileInfo.MinMachineDayticks
    private val Date2k0102 = LocalDate.of(2000, 1, 2)

    private val BypassCriteria = FileInfoFilter.Criteria.Bypass
    private val From2k0102Criteria = FileInfoFilter.Criteria.Bypass.copy(fromDateCondition = Date2k0102)
    private val Until2k0102Criteria = FileInfoFilter.Criteria.Bypass.copy(untilDateCondition = Date2k0102)

    private val FileInfo2k0101 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, date = Date2k0101Ticks)
    private val FileInfo2k0102 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, date = Date2k0101Ticks + 1)
    private val FileInfo2k0103 = FileInfo(folder = "FOLDER", name = "NAME", size = 0L, date = Date2k0101Ticks + 2)

    private fun fileWithName(n: String): FileInfo {
        return FileInfo(folder = "FOLDER", name = n, size = 0L, date = Date2k0101Ticks)
    }
    private fun filter(vararg n: String): FileInfoFilter.Criteria {
        return FileInfoFilter.Criteria.Bypass.copy(fileNameConditions = n.asSequence())
    }

    @Test
    fun filesFilter_shouldCorrectlyKeepAllFilesInBypassMode() {
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0101, BypassCriteria))
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0102, BypassCriteria))
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0103, BypassCriteria))
    }

    @Test
    fun filesFilter_shouldCorrectlyKeepAllFilesBeforeAndEqualFromCriteria() {
        assertFalse(FileInfoFilter.isFileEligible(FileInfo2k0101, From2k0102Criteria))
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0102, From2k0102Criteria))
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0103, From2k0102Criteria))
    }

    @Test
    fun filesFilter_shouldCorrectlyKeepAllFilesAfterAndEqualToUntilCriteria() {
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0101, Until2k0102Criteria))
        assertTrue(FileInfoFilter.isFileEligible(FileInfo2k0102, Until2k0102Criteria))
        assertFalse(FileInfoFilter.isFileEligible(FileInfo2k0103, Until2k0102Criteria))
    }

    @Test
    fun filesFilter_shouldCorrectlyFilterAllFilesAfterWithExtensionAvi() {
        assertFalse(FileInfoFilter.isFileEligible(fileWithName("xx.avo"), filter("*.avi")))
        assertTrue(FileInfoFilter.isFileEligible(fileWithName("xx.AVI"), filter("*.avi"))) // non case-sensitive
        assertTrue(FileInfoFilter.isFileEligible(fileWithName("xx.avi"), filter("*.avi")))
    }

    @Test
    fun filesFilter_shouldCorrectlyFilterAllFilesAfterContainingXx() {
        assertTrue(FileInfoFilter.isFileEligible(fileWithName("xx.avo"), filter("*XX*"))) // non case-sensitive
        assertFalse(FileInfoFilter.isFileEligible(fileWithName("xyxyxy.AVI"), filter("*XX*")))
        assertTrue(FileInfoFilter.isFileEligible(fileWithName("xxXXxx.avi"), filter("*XX*")))
    }

    @Test
    fun filesFilter_shouldCorrectlyFilterAllFilesWithCombinedFiltersOneOrTheOther() {
        assertFalse(FileInfoFilter.isFileEligible(fileWithName("xx.avo"), filter("*this*", "*.avi")))
        assertTrue(FileInfoFilter.isFileEligible(fileWithName("xx.avi"), filter("*this*", "*.avi")))
        assertTrue(FileInfoFilter.isFileEligible(fileWithName("xxthispp.avo"), filter("*this*", "*.avi")))
    }
}