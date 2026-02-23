package com.coderabbit.calendar;

import java.time.LocalDate;

public record NavContext(
    String viewMode,
    int ctxYear,
    int ctxMonth,
    int ctxDay,
    int ctxIsoYear,
    int ctxIsoWeek,
    LocalDate today,
    int todayIsoYear,
    int todayIsoWeek
) {
    public static NavContext of(String viewMode, int ctxYear, int ctxMonth, int ctxDay,
                                int ctxIsoYear, int ctxIsoWeek) {
        LocalDate today = LocalDate.now();
        int[] todayIso = CalendarUtils.getIsoWeek(today);
        return new NavContext(viewMode, ctxYear, ctxMonth, ctxDay,
                              ctxIsoYear, ctxIsoWeek, today, todayIso[0], todayIso[1]);
    }
}
