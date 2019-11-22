package org.szymonbultrowicz.olympusphototransfer.lib.client

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class BinaryDateConverter {

    private fun maskShift(i: Int, upperMaskBitPos: Int, lowerMaskBitPos: Int): Int =
        (i % (2 shl upperMaskBitPos)) shr lowerMaskBitPos

    private fun binaryToDate(binaryDate: Int): LocalDate {
        // ..yyyyyyyymmmmddddd
        //   65432109876543210
        val days = maskShift(binaryDate, 4, 0)
        val months = maskShift(binaryDate, 8, 5)
        val years = maskShift(binaryDate, 16, 9) + 1980
        return LocalDate.of(years, months, days)
    }

    private fun binaryToTime(binaryTime: Int): LocalTime {
        // ...hhhhhhmmmmmmsssss
        //    65432109876543210
        val s = maskShift(binaryTime, 4, 0)
        val m = maskShift(binaryTime, 10, 5)
        val h = maskShift(binaryTime, 16, 11)
        return LocalTime.of(h, m, s)
    }

    fun binaryToDateTime(binaryDate: Int, binaryTime: Int): LocalDateTime {
        val date = binaryToDate(binaryDate)
        val time = binaryToTime(binaryTime)
        return time.atDate(date)
    }
}