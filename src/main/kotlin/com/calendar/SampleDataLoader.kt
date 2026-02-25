package com.calendar

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalTime

// Review fix: only seed sample data when the "demo" profile is active
@Component
@Profile("demo")
class SampleDataLoader(private val eventService: EventService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val today = LocalDate.now()

        eventService.create(Event(
            title = "Team Standup",
            description = "Daily sync with the engineering team",
            date = today,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 30),
            color = "#1a73e8"
        ))

        eventService.create(Event(
            title = "Lunch Break",
            description = "Grab food from the cafeteria",
            date = today,
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(13, 0),
            color = "#34a853"
        ))

        eventService.create(Event(
            title = "Code Review",
            description = "Review pull requests and provide feedback",
            date = today,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            color = "#ea4335"
        ))

        eventService.create(Event(
            title = "Sprint Planning",
            description = "Plan tasks for the upcoming sprint",
            date = today.plusDays(1),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 30),
            color = "#fbbc04"
        ))

        eventService.create(Event(
            title = "Design Review",
            description = "Walk through the new UI mockups",
            date = today.plusDays(2),
            startTime = LocalTime.of(15, 0),
            endTime = LocalTime.of(16, 0),
            color = "#9c27b0"
        ))
    }
}
