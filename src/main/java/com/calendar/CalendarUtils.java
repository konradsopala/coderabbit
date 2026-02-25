package com.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CalendarUtils {

    private CalendarUtils() {}

    public static List<List<DayInfo>> monthGrid(int year, int month) {
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());

        // Find Sunday on or before the first of the month
        LocalDate start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        List<List<DayInfo>> weeks = new ArrayList<>();
        LocalDate current = start;

        while (current.isBefore(last) || current.isEqual(last) || current.getDayOfWeek() != DayOfWeek.SUNDAY) {
            List<DayInfo> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                boolean isCurrentMonth = current.getMonthValue() == month && current.getYear() == year;
                week.add(new DayInfo(current, current.getDayOfMonth(), current.getMonthValue(), current.getYear(), isCurrentMonth));
                current = current.plusDays(1);
            }
            weeks.add(week);
            if (weeks.size() >= 6) break;
        }

        return weeks;
    }

    public static List<LocalDate> weekDates(LocalDate date) {
        LocalDate sunday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dates.add(sunday.plusDays(i));
        }
        return dates;
    }

    public static LocalDate weekStartFromIso(int year, int week) {
        WeekFields wf = WeekFields.of(Locale.US);
        LocalDate jan1 = LocalDate.of(year, 1, 1);
        LocalDate firstWeekStart = jan1.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        return firstWeekStart.plusWeeks(week - 1);
    }

    public static int isoWeekNumber(LocalDate date) {
        WeekFields wf = WeekFields.of(Locale.US);
        return date.get(wf.weekOfYear());
    }

    public static List<HourInfo> hours() {
        List<HourInfo> list = new ArrayList<>();
        for (int h = 6; h < 24; h++) {
            list.add(new HourInfo(h, formatHour(h)));
        }
        return list;
    }

    public static String formatHour(int hour) {
        if (hour == 0 || hour == 12) {
            return (hour == 0 ? "12" : "12") + (hour < 12 ? " AM" : " PM");
        }
        int h = hour % 12;
        return h + (hour < 12 ? " AM" : " PM");
    }

    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    public static String monthYearLabel(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    public static int[] prevMonth(int year, int month) {
        if (month == 1) return new int[]{year - 1, 12};
        return new int[]{year, month - 1};
    }

    public static int[] nextMonth(int year, int month) {
        if (month == 12) return new int[]{year + 1, 1};
        return new int[]{year, month + 1};
    }

    public static String weekLabel(List<LocalDate> dates) {
        LocalDate first = dates.get(0);
        LocalDate last = dates.get(6);
        DateTimeFormatter monthDay = DateTimeFormatter.ofPattern("MMM d");
        if (first.getYear() != last.getYear()) {
            return first.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + " – " +
                   last.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        }
        if (first.getMonthValue() != last.getMonthValue()) {
            return first.format(monthDay) + " – " + last.format(monthDay) + ", " + last.getYear();
        }
        return first.format(monthDay) + " – " + last.getDayOfMonth() + ", " + last.getYear();
    }

    public static String dayLabel(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
    }
}
