package com.calendar

import java.time.LocalDate

data class DayInfo(
    val date: LocalDate,
    val day: Int,
    val month: Int,
    val year: Int,
    val currentMonth: Boolean
)
