package com.coderabbit.calendar

import java.time.LocalDate

data class DayInfo(
    val year: Int,
    val month: Int,
    val day: Int,
    val isCurrentMonth: Boolean
)

data class HourInfo(
    val hour: Int,
    val label: String
)

data class NavContext(
    val viewMode: String,
    val ctxYear: Int,
    val ctxMonth: Int,
    val ctxDay: Int,
    val ctxIsoYear: Int,
    val ctxIsoWeek: Int,
    val today: LocalDate,
    val todayIsoYear: Int,
    val todayIsoWeek: Int
) {
    companion object {
        fun of(
            viewMode: String, ctxYear: Int, ctxMonth: Int, ctxDay: Int,
            ctxIsoYear: Int, ctxIsoWeek: Int
        ): NavContext {
            val today = LocalDate.now()
            val todayIso = CalendarUtils.getIsoWeek(today)
            return NavContext(
                viewMode, ctxYear, ctxMonth, ctxDay,
                ctxIsoYear, ctxIsoWeek, today, todayIso[0], todayIso[1]
            )
        }
    }
}
