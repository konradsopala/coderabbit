package com.coderabbit.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CalendarUtils {

    private static final String[] MONTH_NAMES = {
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    private static final String[] MONTH_ABBREVS = {
        "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private CalendarUtils() {
    }

    public static String formatHour(int h) {
        if (h == 0) return "12 AM";
        if (h < 12) return h + " AM";
        if (h == 12) return "12 PM";
        return (h - 12) + " PM";
    }

    public static List<HourInfo> getHours() {
        List<HourInfo> hours = new ArrayList<>();
        for (int h = 6; h < 24; h++) {
            hours.add(new HourInfo(h, formatHour(h)));
        }
        return hours;
    }

    public static int[] prevMonth(int year, int month) {
        if (month == 1) return new int[]{year - 1, 12};
        return new int[]{year, month - 1};
    }

    public static int[] nextMonth(int year, int month) {
        if (month == 12) return new int[]{year + 1, 1};
        return new int[]{year, month + 1};
    }

    public static List<List<DayInfo>> getMonthGrid(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        // Sunday-start: Sun=0, Mon=1, ..., Sat=6
        int startDow = firstDay.getDayOfWeek().getValue() % 7; // Mon=1..Sun=7 -> Sun=0,Mon=1..Sat=6
        LocalDate startDate = firstDay.minusDays(startDow);

        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        List<List<DayInfo>> weeks = new ArrayList<>();
        LocalDate current = startDate;

        while (true) {
            List<DayInfo> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                week.add(new DayInfo(
                    current.getYear(),
                    current.getMonthValue(),
                    current.getDayOfMonth(),
                    current.getMonthValue() == month && current.getYear() == year
                ));
                current = current.plusDays(1);
            }
            weeks.add(week);

            // Stop after we've passed the last day and current is a Sunday
            if (current.isAfter(lastDay) && current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                break;
            }
        }

        return weeks;
    }

    public static int[] getIsoWeek(LocalDate d) {
        int isoYear = d.get(IsoFields.WEEK_BASED_YEAR);
        int isoWeek = d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return new int[]{isoYear, isoWeek};
    }

    public static List<LocalDate> getWeekDates(int isoYear, int isoWeek) {
        // Find Monday of the ISO week
        LocalDate jan4 = LocalDate.of(isoYear, 1, 4);
        int jan4IsoDow = jan4.getDayOfWeek().getValue(); // Mon=1..Sun=7
        LocalDate monday = jan4.plusDays((long)(isoWeek - 1) * 7 - (jan4IsoDow - 1));

        // Go back to Sunday for Sunday-start week
        LocalDate sunday = monday.minusDays(1);

        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dates.add(sunday.plusDays(i));
        }
        return dates;
    }

    public static LocalDate isoWeekToDate(int isoYear, int isoWeek) {
        LocalDate jan4 = LocalDate.of(isoYear, 1, 4);
        int jan4IsoDow = jan4.getDayOfWeek().getValue();
        LocalDate monday = jan4.plusDays((long)(isoWeek - 1) * 7 - (jan4IsoDow - 1));
        return monday.plusDays(2); // Wednesday
    }

    public static String monthName(int month) {
        return MONTH_NAMES[month];
    }

    public static String dayName(LocalDate d) {
        return d.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public static String dayAbbrev(LocalDate d) {
        return d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    public static String formatDate(LocalDate d) {
        return dayName(d) + ", " + MONTH_NAMES[d.getMonthValue()] + " " + d.getDayOfMonth() + ", " + d.getYear();
    }

    public static String formatWeekLabel(List<LocalDate> dates) {
        LocalDate start = dates.get(0);
        LocalDate end = dates.get(6);
        String startStr = MONTH_ABBREVS[start.getMonthValue()] + " " + start.getDayOfMonth();
        if (start.getMonthValue() == end.getMonthValue()) {
            return startStr + " - " + end.getDayOfMonth() + ", " + end.getYear();
        }
        String endStr = MONTH_ABBREVS[end.getMonthValue()] + " " + end.getDayOfMonth() + ", " + end.getYear();
        return startStr + " - " + endStr;
    }
}
