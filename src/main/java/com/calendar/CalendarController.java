package com.calendar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class CalendarController {

    private final EventService eventService;

    public CalendarController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String index() {
        LocalDate today = LocalDate.now();
        return "redirect:/month/" + today.getYear() + "/" + today.getMonthValue();
    }

    @GetMapping("/month/{year}/{month}")
    public String month(@PathVariable int year, @PathVariable int month, Model model) {
        if (month < 1 || month > 12) {
            LocalDate today = LocalDate.now();
            return "redirect:/month/" + today.getYear() + "/" + today.getMonthValue();
        }

        List<List<DayInfo>> weeks = CalendarUtils.monthGrid(year, month);
        int[] prev = CalendarUtils.prevMonth(year, month);
        int[] next = CalendarUtils.nextMonth(year, month);

        // Gather all dates in the grid to query events
        LocalDate gridStart = weeks.get(0).get(0).date();
        LocalDate gridEnd = weeks.get(weeks.size() - 1).get(6).date();
        Map<LocalDate, List<Event>> eventsByDate = eventService.findByDateRange(gridStart, gridEnd);

        model.addAttribute("title", CalendarUtils.monthYearLabel(year, month));
        model.addAttribute("weeks", weeks);
        model.addAttribute("prevUrl", "/month/" + prev[0] + "/" + prev[1]);
        model.addAttribute("nextUrl", "/month/" + next[0] + "/" + next[1]);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("currentMonth", month);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("viewMode", "month");
        model.addAttribute("weekNum", CalendarUtils.isoWeekNumber(LocalDate.now()));
        model.addAttribute("eventsByDate", eventsByDate);

        return "month";
    }

    @GetMapping("/week/{year}/{week}")
    public String week(@PathVariable int year, @PathVariable int week, Model model) {
        if (week < 1 || week > 53) {
            LocalDate today = LocalDate.now();
            return "redirect:/week/" + today.getYear() + "/" + CalendarUtils.isoWeekNumber(today);
        }

        LocalDate weekStart = CalendarUtils.weekStartFromIso(year, week);
        List<LocalDate> dates = CalendarUtils.weekDates(weekStart);
        List<HourInfo> hours = CalendarUtils.hours();
        int currentHour = LocalTime.now().getHour();

        int prevWeek = week - 1;
        int prevYear = year;
        if (prevWeek < 1) {
            prevYear--;
            prevWeek = 52;
        }
        int nextWeek = week + 1;
        int nextYear = year;
        if (nextWeek > 52) {
            nextYear++;
            nextWeek = 1;
        }

        Map<LocalDate, List<Event>> eventsByDate = eventService.findByDateRange(dates.get(0), dates.get(6));

        model.addAttribute("title", CalendarUtils.weekLabel(dates));
        model.addAttribute("dates", dates);
        model.addAttribute("hours", hours);
        model.addAttribute("prevUrl", "/week/" + prevYear + "/" + prevWeek);
        model.addAttribute("nextUrl", "/week/" + nextYear + "/" + nextWeek);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("currentHour", currentHour);
        model.addAttribute("viewMode", "week");
        model.addAttribute("year", year);
        model.addAttribute("month", LocalDate.now().getMonthValue());
        model.addAttribute("weekNum", week);
        model.addAttribute("eventsByDate", eventsByDate);

        return "week";
    }

    @GetMapping("/day/{year}/{month}/{day}")
    public String day(@PathVariable int year, @PathVariable int month, @PathVariable int day, Model model) {
        LocalDate date;
        try {
            date = LocalDate.of(year, month, day);
        } catch (Exception e) {
            LocalDate today = LocalDate.now();
            return "redirect:/day/" + today.getYear() + "/" + today.getMonthValue() + "/" + today.getDayOfMonth();
        }

        List<HourInfo> hours = CalendarUtils.hours();
        LocalDate prev = date.minusDays(1);
        LocalDate next = date.plusDays(1);
        int currentHour = LocalTime.now().getHour();

        List<Event> dayEvents = eventService.findByDate(date);

        model.addAttribute("title", CalendarUtils.dayLabel(date));
        model.addAttribute("date", date);
        model.addAttribute("hours", hours);
        model.addAttribute("prevUrl", "/day/" + prev.getYear() + "/" + prev.getMonthValue() + "/" + prev.getDayOfMonth());
        model.addAttribute("nextUrl", "/day/" + next.getYear() + "/" + next.getMonthValue() + "/" + next.getDayOfMonth());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("currentHour", currentHour);
        model.addAttribute("isToday", CalendarUtils.isToday(date));
        model.addAttribute("viewMode", "day");
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("weekNum", CalendarUtils.isoWeekNumber(date));
        model.addAttribute("events", dayEvents);

        return "day";
    }
}
