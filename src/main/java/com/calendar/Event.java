package com.calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Event {

    private String id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String color;

    public Event() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.color = "#1a73e8";
    }

    public Event(String title, String description, LocalDate date,
                 LocalTime startTime, LocalTime endTime, String color) {
        this();
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        if (color != null && !color.isBlank()) {
            this.color = color;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTimeRange() {
        if (startTime == null) return "";
        String start = formatTime(startTime);
        if (endTime == null) return start;
        return start + " – " + formatTime(endTime);
    }

    private String formatTime(LocalTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        String amPm = hour < 12 ? "AM" : "PM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        if (minute == 0) return displayHour + " " + amPm;
        return displayHour + ":" + String.format("%02d", minute) + " " + amPm;
    }
}
