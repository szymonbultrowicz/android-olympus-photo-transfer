package org.szymonbultrowicz.olympusphototransfer.client

import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class FileInfo (
    val folder: String,
    val name: String,
    val size: Long,
    val date: Int = DefaultDate,
    val time: Int = DefaultTime,
    val thumbnailUrl: URL? = null // if local, no thumbnail will be available
) {

    fun getFileId(): String {
        return "$folder/$name"
    }

    val humanDate: LocalDate = run {
        // ..yyyyyyyymmmmddddd
        //   65432109876543210
        val days = maskShift(date, 4, 0)
        val months = maskShift(date, 8, 5)
        val years = maskShift(date, 16, 9) + 1980
        LocalDate.of(years, months, days)
    }

    val humanTime: LocalTime = run {
        // ...hhhhhhmmmmmmsssss
        //    65432109876543210
        val s = maskShift(time, 4, 0)
        val m = maskShift(time, 10, 5)
        val h = maskShift(time, 16, 11)
        LocalTime.of(h, m, s)
    }

    val humanDateTime: LocalDateTime = humanDate.atTime(humanTime)

    companion object {
        val MaskDays = Integer.parseInt("0000000000011111", 2)
        val MaskMont = Integer.parseInt("0000000111100000", 2)
        val MaskYear = Integer.parseInt("1111111000000000", 2)

        val MaxMachineDayticks = 61343
        val MinMachineDayticks = 10273

        val DefaultDate = MinMachineDayticks
        val DefaultTime = 0

        val MaxDate = LocalDate.of(2099, 12, 31)
        val MinDate = LocalDate.of(2000, 1, 1)

        fun maskShift(i: Int, upperMaskBitPos: Int, lowerMaskBitPos: Int): Int =
            (i % (2 shl upperMaskBitPos)) shr lowerMaskBitPos
    }
}
