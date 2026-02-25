package com.calendar;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final Map<String, Event> events = new ConcurrentHashMap<>();

    public Event create(Event event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalArgumentException("Event title is required");
        }
        if (event.getDate() == null) {
            throw new IllegalArgumentException("Event date is required");
        }
        if (event.getStartTime() != null && event.getEndTime() != null
                && event.getEndTime().isBefore(event.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        events.put(event.getId(), event);
        return event;
    }

    public Optional<Event> findById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    public List<Event> findByDate(LocalDate date) {
        return events.values().stream()
                .filter(e -> e.getDate().equals(date))
                .sorted(Comparator.comparing(
                        e -> e.getStartTime() != null ? e.getStartTime() : java.time.LocalTime.MAX))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, List<Event>> findByDateRange(LocalDate start, LocalDate end) {
        return events.values().stream()
                .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                .collect(Collectors.groupingBy(Event::getDate));
    }

    public List<Event> findAll() {
        List<Event> all = new ArrayList<>(events.values());
        all.sort(Comparator.comparing(Event::getDate)
                .thenComparing(e -> e.getStartTime() != null ? e.getStartTime() : java.time.LocalTime.MAX));
        return Collections.unmodifiableList(all);
    }

    public Event update(String id, Event updated) {
        Event existing = events.get(id);
        if (existing == null) {
            throw new EventNotFoundException("Event not found: " + id);
        }
        if (updated.getTitle() != null && !updated.getTitle().isBlank()) {
            existing.setTitle(updated.getTitle());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getDate() != null) {
            existing.setDate(updated.getDate());
        }
        if (updated.getStartTime() != null) {
            existing.setStartTime(updated.getStartTime());
        }
        if (updated.getEndTime() != null) {
            existing.setEndTime(updated.getEndTime());
        }
        if (updated.getColor() != null && !updated.getColor().isBlank()) {
            existing.setColor(updated.getColor());
        }
        return existing;
    }

    public boolean delete(String id) {
        return events.remove(id) != null;
    }

    public int count() {
        return events.size();
    }
}
