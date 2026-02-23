package com.coderabbit.calendar

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDate
import java.time.LocalDateTime

@Controller
class CalendarController(private val weatherService: WeatherService) {

    @GetMapping("/")
    fun index(): String {
        val today = LocalDate.now()
        return "redirect:/month/${today.year}/${today.monthValue}"
    }

    @GetMapping("/month/{year}/{month}")
    fun monthView(@PathVariable year: Int, @PathVariable month: Int, model: Model): String {
        if (month < 1) return "redirect:/month/${year - 1}/12"
        if (month > 12) return "redirect:/month/${year + 1}/1"

        val weeks = CalendarUtils.getMonthGrid(year, month)
        val prev = CalendarUtils.prevMonth(year, month)
        val next = CalendarUtils.nextMonth(year, month)
        val today = LocalDate.now()

        val ctxDate = LocalDate.of(year, month, 15)
        val ctxIso = CalendarUtils.getIsoWeek(ctxDate)

        model.addAttribute("year", year)
        model.addAttribute("month", month)
        model.addAttribute("weeks", weeks)
        model.addAttribute("title", "${CalendarUtils.monthName(month)} $year")
        model.addAttribute("prevYear", prev[0])
        model.addAttribute("prevMonth", prev[1])
        model.addAttribute("nextYear", next[0])
        model.addAttribute("nextMonth", next[1])
        model.addAttribute("today", today)
        model.addAttribute("dayHeaders", arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"))
        model.addAttribute("nav", NavContext.of("month", year, month, 15, ctxIso[0], ctxIso[1]))
        model.addAttribute("weatherMap", weatherService.getForecasts())

        return "month"
    }

    @GetMapping("/week/{year}/{week}")
    fun weekView(@PathVariable year: Int, @PathVariable week: Int, model: Model): String {
        if (week < 1 || week > 53 || year <= 0) return "redirect:/"

        val dates = CalendarUtils.getWeekDates(year, week)
        val today = LocalDate.now()
        val nowHour = LocalDateTime.now().hour
        val hours = CalendarUtils.getHours()
        val weekLabel = CalendarUtils.formatWeekLabel(dates)

        val prevDay = dates[0].minusDays(6)
        val prevIso = CalendarUtils.getIsoWeek(prevDay)

        val nextDay = dates[0].plusDays(8)
        val nextIso = CalendarUtils.getIsoWeek(nextDay)

        val representative = CalendarUtils.isoWeekToDate(year, week)

        model.addAttribute("year", year)
        model.addAttribute("week", week)
        model.addAttribute("dates", dates)
        model.addAttribute("today", today)
        model.addAttribute("nowHour", nowHour)
        model.addAttribute("hours", hours)
        model.addAttribute("title", weekLabel)
        model.addAttribute("prevYear", prevIso[0])
        model.addAttribute("prevWeek", prevIso[1])
        model.addAttribute("nextYear", nextIso[0])
        model.addAttribute("nextWeek", nextIso[1])
        model.addAttribute("nav", NavContext.of(
            "week", representative.year, representative.monthValue,
            representative.dayOfMonth, year, week
        ))
        model.addAttribute("weatherMap", weatherService.getForecasts())

        return "week"
    }

    @GetMapping("/day/{year}/{month}/{day}")
    fun dayView(
        @PathVariable year: Int,
        @PathVariable month: Int,
        @PathVariable day: Int,
        model: Model
    ): String {
        val d = try {
            LocalDate.of(year, month, day)
        } catch (_: Exception) {
            return "redirect:/"
        }

        val today = LocalDate.now()
        val isToday = d == today
        val nowHour = LocalDateTime.now().hour
        val hours = CalendarUtils.getHours()

        val prevDate = d.minusDays(1)
        val nextDate = d.plusDays(1)

        val ctxIso = CalendarUtils.getIsoWeek(d)

        model.addAttribute("date", d)
        model.addAttribute("year", year)
        model.addAttribute("month", month)
        model.addAttribute("day", day)
        model.addAttribute("isToday", isToday)
        model.addAttribute("nowHour", nowHour)
        model.addAttribute("hours", hours)
        model.addAttribute("title", CalendarUtils.formatDate(d))
        model.addAttribute("dayNameStr", CalendarUtils.dayName(d))
        model.addAttribute("prevDate", prevDate)
        model.addAttribute("nextDate", nextDate)
        model.addAttribute("nav", NavContext.of("day", year, month, day, ctxIso[0], ctxIso[1]))
        model.addAttribute("weather", weatherService.getWeatherForDate(d))

        return "day"
    }
}
