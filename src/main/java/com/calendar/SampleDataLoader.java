package com.calendar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final EventService eventService;

    public SampleDataLoader(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void run(String... args) {
        LocalDate today = LocalDate.now();

        eventService.create(new Event(
                "Team Standup",
                "Daily sync with the engineering team",
                today,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                "#1a73e8"
        ));

        eventService.create(new Event(
                "Lunch Break",
                "Grab food from the cafeteria",
                today,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                "#34a853"
        ));

        eventService.create(new Event(
                "Code Review",
                "Review pull requests and provide feedback",
                today,
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                "#ea4335"
        ));

        eventService.create(new Event(
                "Sprint Planning",
                "Plan tasks for the upcoming sprint",
                today.plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                "#fbbc04"
        ));

        eventService.create(new Event(
                "Design Review",
                "Walk through the new UI mockups",
                today.plusDays(2),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                "#9c27b0"
        ));
    }
}
