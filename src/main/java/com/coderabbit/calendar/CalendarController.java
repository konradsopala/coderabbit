package com.coderabbit.calendar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CalendarController {

    @GetMapping("/")
    public String index() {
        LocalDate today = LocalDate.now();
        return "redirect:/month/" + today.getYear() + "/" + today.getMonthValue();
    }

    @GetMapping("/month/{year}/{month}")
    public String monthView(@PathVariable int year, @PathVariable int month, Model model) {
        if (month < 1) {
            return "redirect:/month/" + (year - 1) + "/12";
        }
        if (month > 12) {
            return "redirect:/month/" + (year + 1) + "/1";
        }

        List<List<DayInfo>> weeks = CalendarUtils.getMonthGrid(year, month);
        int[] prev = CalendarUtils.prevMonth(year, month);
        int[] next = CalendarUtils.nextMonth(year, month);
        LocalDate today = LocalDate.now();

        LocalDate ctxDate = LocalDate.of(year, month, 15);
        int[] ctxIso = CalendarUtils.getIsoWeek(ctxDate);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("weeks", weeks);
        model.addAttribute("title", CalendarUtils.monthName(month) + " " + year);
        model.addAttribute("prevYear", prev[0]);
        model.addAttribute("prevMonth", prev[1]);
        model.addAttribute("nextYear", next[0]);
        model.addAttribute("nextMonth", next[1]);
        model.addAttribute("today", today);
        model.addAttribute("dayHeaders", new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"});
        model.addAttribute("nav", NavContext.of("month", year, month, 15, ctxIso[0], ctxIso[1]));

        return "month";
    }

    @GetMapping("/week/{year}/{week}")
    public String weekView(@PathVariable int year, @PathVariable int week, Model model) {
        if (week < 1 || week > 53 || year <= 0) {
            return "redirect:/";
        }

        List<LocalDate> dates = CalendarUtils.getWeekDates(year, week);
        LocalDate today = LocalDate.now();
        int nowHour = LocalDateTime.now().getHour();
        List<HourInfo> hours = CalendarUtils.getHours();
        String weekLabel = CalendarUtils.formatWeekLabel(dates);

        // Previous week
        LocalDate prevDay = dates.get(0).minusDays(6);
        int[] prevIso = CalendarUtils.getIsoWeek(prevDay);

        // Next week
        LocalDate nextDay = dates.get(0).plusDays(8);
        int[] nextIso = CalendarUtils.getIsoWeek(nextDay);

        // Cross-view context
        LocalDate representative = CalendarUtils.isoWeekToDate(year, week);

        model.addAttribute("year", year);
        model.addAttribute("week", week);
        model.addAttribute("dates", dates);
        model.addAttribute("today", today);
        model.addAttribute("nowHour", nowHour);
        model.addAttribute("hours", hours);
        model.addAttribute("title", weekLabel);
        model.addAttribute("prevYear", prevIso[0]);
        model.addAttribute("prevWeek", prevIso[1]);
        model.addAttribute("nextYear", nextIso[0]);
        model.addAttribute("nextWeek", nextIso[1]);
        model.addAttribute("nav", NavContext.of("week", representative.getYear(),
                representative.getMonthValue(), representative.getDayOfMonth(), year, week));

        return "week";
    }

    @GetMapping("/day/{year}/{month}/{day}")
    public String dayView(@PathVariable int year, @PathVariable int month,
                          @PathVariable int day, Model model) {
        LocalDate d;
        try {
            d = LocalDate.of(year, month, day);
        } catch (Exception e) {
            return "redirect:/";
        }

        LocalDate today = LocalDate.now();
        boolean isToday = d.equals(today);
        int nowHour = LocalDateTime.now().getHour();
        List<HourInfo> hours = CalendarUtils.getHours();

        LocalDate prevDate = d.minusDays(1);
        LocalDate nextDate = d.plusDays(1);

        int[] ctxIso = CalendarUtils.getIsoWeek(d);

        model.addAttribute("date", d);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        model.addAttribute("isToday", isToday);
        model.addAttribute("nowHour", nowHour);
        model.addAttribute("hours", hours);
        model.addAttribute("title", CalendarUtils.formatDate(d));
        model.addAttribute("dayNameStr", CalendarUtils.dayName(d));
        model.addAttribute("prevDate", prevDate);
        model.addAttribute("nextDate", nextDate);
        model.addAttribute("nav", NavContext.of("day", year, month, day, ctxIso[0], ctxIso[1]));

        return "day";
    }
}
