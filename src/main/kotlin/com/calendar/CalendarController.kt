package com.calendar

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDate
import java.time.LocalTime

@Controller
class CalendarController(private val eventService: EventService) {

    @GetMapping("/")
    fun index(): String {
        val today = LocalDate.now()
        return "redirect:/month/${today.year}/${today.monthValue}"
    }

    @GetMapping("/month/{year}/{month}")
    fun month(@PathVariable year: Int, @PathVariable month: Int, model: Model): String {
        if (month < 1 || month > 12) {
            val today = LocalDate.now()
            return "redirect:/month/${today.year}/${today.monthValue}"
        }

        val weeks = CalendarUtils.monthGrid(year, month)
        val prev = CalendarUtils.prevMonth(year, month)
        val next = CalendarUtils.nextMonth(year, month)

        val gridStart = weeks[0][0].date
        val gridEnd = weeks.last().last().date
        val eventsByDate = eventService.findByDateRange(gridStart, gridEnd)

        model.addAttribute("title", CalendarUtils.monthYearLabel(year, month))
        model.addAttribute("weeks", weeks)
        model.addAttribute("prevUrl", "/month/${prev[0]}/${prev[1]}")
        model.addAttribute("nextUrl", "/month/${next[0]}/${next[1]}")
        model.addAttribute("today", LocalDate.now())
        model.addAttribute("currentMonth", month)
        model.addAttribute("year", year)
        model.addAttribute("month", month)
        model.addAttribute("viewMode", "month")
        model.addAttribute("weekNum", CalendarUtils.isoWeekNumber(LocalDate.now()))
        model.addAttribute("eventsByDate", eventsByDate)

        return "month"
    }

    @GetMapping("/week/{year}/{week}")
    fun week(@PathVariable year: Int, @PathVariable week: Int, model: Model): String {
        if (week < 1 || week > 53) {
            val today = LocalDate.now()
            return "redirect:/week/${today.year}/${CalendarUtils.isoWeekNumber(today)}"
        }

        val weekStart = CalendarUtils.weekStartFromIso(year, week)
        val dates = CalendarUtils.weekDates(weekStart)
        val hours = CalendarUtils.hours()
        val currentHour = LocalTime.now().hour

        var prevWeek = week - 1
        var prevYear = year
        if (prevWeek < 1) { prevYear--; prevWeek = 52 }
        var nextWeek = week + 1
        var nextYear = year
        if (nextWeek > 52) { nextYear++; nextWeek = 1 }

        val eventsByDate = eventService.findByDateRange(dates[0], dates[6])

        model.addAttribute("title", CalendarUtils.weekLabel(dates))
        model.addAttribute("dates", dates)
        model.addAttribute("hours", hours)
        model.addAttribute("prevUrl", "/week/$prevYear/$prevWeek")
        model.addAttribute("nextUrl", "/week/$nextYear/$nextWeek")
        model.addAttribute("today", LocalDate.now())
        model.addAttribute("currentHour", currentHour)
        model.addAttribute("viewMode", "week")
        model.addAttribute("year", year)
        model.addAttribute("month", LocalDate.now().monthValue)
        model.addAttribute("weekNum", week)
        model.addAttribute("eventsByDate", eventsByDate)

        return "week"
    }

    @GetMapping("/day/{year}/{month}/{day}")
    fun day(@PathVariable year: Int, @PathVariable month: Int, @PathVariable day: Int, model: Model): String {
        val date = try {
            LocalDate.of(year, month, day)
        } catch (e: Exception) {
            val today = LocalDate.now()
            return "redirect:/day/${today.year}/${today.monthValue}/${today.dayOfMonth}"
        }

        val hours = CalendarUtils.hours()
        val prev = date.minusDays(1)
        val next = date.plusDays(1)
        val currentHour = LocalTime.now().hour
        val dayEvents = eventService.findByDate(date)

        model.addAttribute("title", CalendarUtils.dayLabel(date))
        model.addAttribute("date", date)
        model.addAttribute("hours", hours)
        model.addAttribute("prevUrl", "/day/${prev.year}/${prev.monthValue}/${prev.dayOfMonth}")
        model.addAttribute("nextUrl", "/day/${next.year}/${next.monthValue}/${next.dayOfMonth}")
        model.addAttribute("today", LocalDate.now())
        model.addAttribute("currentHour", currentHour)
        model.addAttribute("isToday", CalendarUtils.isToday(date))
        model.addAttribute("viewMode", "day")
        model.addAttribute("year", year)
        model.addAttribute("month", month)
        model.addAttribute("weekNum", CalendarUtils.isoWeekNumber(date))
        model.addAttribute("events", dayEvents)

        return "day"
    }
}
