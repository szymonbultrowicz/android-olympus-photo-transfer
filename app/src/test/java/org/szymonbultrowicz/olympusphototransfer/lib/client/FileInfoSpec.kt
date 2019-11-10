package org.szymonbultrowicz.olympusphototransfer.lib.client

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

val DEFAULT_FILE_INFO = FileInfo(
    folder = "FOLDER",
    name = "NAME",
    size = 0L
)

class FileInfoTest {

    @Test
    fun filesInfo_shouldCorrectlyRetrieveTheDataFromAMachineDate() {
        assertEquals(FileInfo.MinDate, DEFAULT_FILE_INFO.copy(date = FileInfo.MinMachineDayticks).humanDate)
        assertEquals(FileInfo.MaxDate, DEFAULT_FILE_INFO.copy(date = FileInfo.MaxMachineDayticks).humanDate)

        assertEquals(LocalDate.of(2050, 9, 29), DEFAULT_FILE_INFO.copy(date = 36157).humanDate)
        assertEquals(LocalDate.of(2050, 9, 28), DEFAULT_FILE_INFO.copy(date = 36156).humanDate)
        assertEquals(LocalDate.of(2025, 1, 1), DEFAULT_FILE_INFO.copy(date = 23073).humanDate)

        assertEquals(LocalDate.of(2000, 1, 1), DEFAULT_FILE_INFO.copy(date = 10273).humanDate)
        assertEquals(LocalDate.of(2000, 2, 1), DEFAULT_FILE_INFO.copy(date = 10305).humanDate)
        assertEquals(LocalDate.of(2000, 3, 1), DEFAULT_FILE_INFO.copy(date = 10337).humanDate)
        assertEquals(LocalDate.of(2001, 3, 1), DEFAULT_FILE_INFO.copy(date = 10849).humanDate)
        assertEquals(LocalDate.of(2002, 3, 1), DEFAULT_FILE_INFO.copy(date = 11361).humanDate)
        assertEquals(LocalDate.of(2019, 1, 14), DEFAULT_FILE_INFO.copy(date = 20014).humanDate)
    }
}