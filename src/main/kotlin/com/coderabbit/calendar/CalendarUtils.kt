package com.coderabbit.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.IsoFields
import java.util.Locale

object CalendarUtils {

    private val MONTH_NAMES = arrayOf(
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val MONTH_ABBREVS = arrayOf(
        "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    fun formatHour(h: Int): String = when {
        h == 0 -> "12 AM"
        h < 12 -> "$h AM"
        h == 12 -> "12 PM"
        else -> "${h - 12} PM"
    }

    fun getHours(): List<HourInfo> = (6 until 24).map { HourInfo(it, formatHour(it)) }

    fun prevMonth(year: Int, month: Int): IntArray =
        if (month == 1) intArrayOf(year - 1, 12) else intArrayOf(year, month - 1)

    fun nextMonth(year: Int, month: Int): IntArray =
        if (month == 12) intArrayOf(year + 1, 1) else intArrayOf(year, month + 1)

    fun getMonthGrid(year: Int, month: Int): List<List<DayInfo>> {
        val firstDay = LocalDate.of(year, month, 1)
        // Sunday-start: Sun=0, Mon=1, ..., Sat=6
        val startDow = firstDay.dayOfWeek.value % 7
        var current = firstDay.minusDays(startDow.toLong())

        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())
        val weeks = mutableListOf<List<DayInfo>>()

        while (true) {
            val week = (0 until 7).map {
                val info = DayInfo(
                    current.year,
                    current.monthValue,
                    current.dayOfMonth,
                    current.monthValue == month && current.year == year
                )
                current = current.plusDays(1)
                info
            }
            weeks.add(week)

            if (current.isAfter(lastDay) && current.dayOfWeek == DayOfWeek.SUNDAY) break
        }

        return weeks
    }

    fun getIsoWeek(d: LocalDate): IntArray {
        val isoYear = d.get(IsoFields.WEEK_BASED_YEAR)
        val isoWeek = d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        return intArrayOf(isoYear, isoWeek)
    }

    fun getWeekDates(isoYear: Int, isoWeek: Int): List<LocalDate> {
        val jan4 = LocalDate.of(isoYear, 1, 4)
        val jan4IsoDow = jan4.dayOfWeek.value // Mon=1..Sun=7
        val monday = jan4.plusDays(((isoWeek - 1) * 7 - (jan4IsoDow - 1)).toLong())
        val sunday = monday.minusDays(1)
        return (0 until 7).map { sunday.plusDays(it.toLong()) }
    }

    fun isoWeekToDate(isoYear: Int, isoWeek: Int): LocalDate {
        val jan4 = LocalDate.of(isoYear, 1, 4)
        val jan4IsoDow = jan4.dayOfWeek.value
        val monday = jan4.plusDays(((isoWeek - 1) * 7 - (jan4IsoDow - 1)).toLong())
        return monday.plusDays(2) // Wednesday
    }

    fun monthName(month: Int): String = MONTH_NAMES[month]

    fun dayName(d: LocalDate): String =
        d.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    fun dayAbbrev(d: LocalDate): String =
        d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

    fun formatDate(d: LocalDate): String =
        "${dayName(d)}, ${MONTH_NAMES[d.monthValue]} ${d.dayOfMonth}, ${d.year}"

    fun formatWeekLabel(dates: List<LocalDate>): String {
        val start = dates[0]
        val end = dates[6]
        val startStr = "${MONTH_ABBREVS[start.monthValue]} ${start.dayOfMonth}"
        return if (start.monthValue == end.monthValue) {
            "$startStr - ${end.dayOfMonth}, ${end.year}"
        } else {
            val endStr = "${MONTH_ABBREVS[end.monthValue]} ${end.dayOfMonth}, ${end.year}"
            "$startStr - $endStr"
        }
    }
}
