package com.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

object CalendarUtils {

    fun monthGrid(year: Int, month: Int): List<List<DayInfo>> {
        val first = LocalDate.of(year, month, 1)
        val last = first.with(TemporalAdjusters.lastDayOfMonth())
        val start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val weeks = mutableListOf<List<DayInfo>>()
        var current = start

        while (current.isBefore(last) || current == last || current.dayOfWeek != DayOfWeek.SUNDAY) {
            val week = (0 until 7).map {
                val isCurrentMonth = current.monthValue == month && current.year == year
                val info = DayInfo(current, current.dayOfMonth, current.monthValue, current.year, isCurrentMonth)
                current = current.plusDays(1)
                info
            }
            weeks.add(week)
            if (weeks.size >= 6) break
        }

        return weeks
    }

    fun weekDates(date: LocalDate): List<LocalDate> {
        val sunday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return (0L until 7L).map { sunday.plusDays(it) }
    }

    fun weekStartFromIso(year: Int, week: Int): LocalDate {
        val jan1 = LocalDate.of(year, 1, 1)
        val firstWeekStart = jan1.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return firstWeekStart.plusWeeks((week - 1).toLong())
    }

    fun isoWeekNumber(date: LocalDate): Int {
        val wf = WeekFields.of(Locale.US)
        return date.get(wf.weekOfYear())
    }

    fun hours(): List<HourInfo> =
        (6 until 24).map { HourInfo(it, formatHour(it)) }

    fun formatHour(hour: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        return "$displayHour $amPm"
    }

    fun isToday(date: LocalDate): Boolean = date == LocalDate.now()

    fun monthYearLabel(year: Int, month: Int): String {
        val date = LocalDate.of(year, month, 1)
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    fun prevMonth(year: Int, month: Int): IntArray =
        if (month == 1) intArrayOf(year - 1, 12) else intArrayOf(year, month - 1)

    fun nextMonth(year: Int, month: Int): IntArray =
        if (month == 12) intArrayOf(year + 1, 1) else intArrayOf(year, month + 1)

    fun weekLabel(dates: List<LocalDate>): String {
        val first = dates[0]
        val last = dates[6]
        val monthDay = DateTimeFormatter.ofPattern("MMM d")
        return when {
            first.year != last.year ->
                "${first.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))} \u2013 " +
                        last.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            first.monthValue != last.monthValue ->
                "${first.format(monthDay)} \u2013 ${last.format(monthDay)}, ${last.year}"
            else ->
                "${first.format(monthDay)} \u2013 ${last.dayOfMonth}, ${last.year}"
        }
    }

    fun dayLabel(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
}
