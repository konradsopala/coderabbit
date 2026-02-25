package com.calendar;

import java.time.LocalDate;

public record DayInfo(LocalDate date, int day, int month, int year, boolean isCurrentMonth) {
}
