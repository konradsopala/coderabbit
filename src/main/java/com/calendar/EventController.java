package com.calendar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> listEvents(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        if (date != null) {
            LocalDate d = LocalDate.parse(date);
            return ResponseEntity.ok(eventService.findByDate(d));
        }

        if (from != null && to != null) {
            LocalDate start = LocalDate.parse(from);
            LocalDate end = LocalDate.parse(to);
            Map<LocalDate, List<Event>> grouped = eventService.findByDateRange(start, end);
            List<Event> flat = grouped.values().stream()
                    .flatMap(List::stream)
                    .sorted((a, b) -> {
                        int cmp = a.getDate().compareTo(b.getDate());
                        if (cmp != 0) return cmp;
                        if (a.getStartTime() == null) return 1;
                        if (b.getStartTime() == null) return -1;
                        return a.getStartTime().compareTo(b.getStartTime());
                    })
                    .toList();
            return ResponseEntity.ok(flat);
        }

        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable String id) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + id));
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event created = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody Event event) {
        Event updated = eventService.update(id, event);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        if (!eventService.delete(id)) {
            throw new EventNotFoundException("Event not found: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
