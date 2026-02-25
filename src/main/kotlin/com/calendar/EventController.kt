package com.calendar

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/api/events")
class EventController(private val eventService: EventService) {

    @GetMapping
    fun listEvents(
        @RequestParam(required = false) date: String?,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?
    ): ResponseEntity<List<Event>> {

        // Review fix: validate date parsing, return 400 on bad format
        if (date != null) {
            val d = parseDate(date, "date")
            return ResponseEntity.ok(eventService.findByDate(d))
        }

        // Review fix: reject partial from/to — both must be present or both absent
        if ((from != null) xor (to != null)) {
            throw ApplicationBadRequestException(
                "Both 'from' and 'to' query parameters are required for range queries"
            )
        }

        if (from != null && to != null) {
            val start = parseDate(from, "from")
            val end = parseDate(to, "to")
            val grouped = eventService.findByDateRange(start, end)
            val flat = grouped.values
                .flatten()
                .sortedWith(compareBy<Event> { it.date }.thenBy { it.startTime ?: java.time.LocalTime.MAX })
            return ResponseEntity.ok(flat)
        }

        return ResponseEntity.ok(eventService.findAll())
    }

    @GetMapping("/{id}")
    fun getEvent(@PathVariable id: String): ResponseEntity<Event> {
        val event = eventService.findById(id)
            ?: throw EventNotFoundException("Event not found: $id")
        return ResponseEntity.ok(event)
    }

    @PostMapping
    fun createEvent(@RequestBody event: Event): ResponseEntity<Event> {
        val created = eventService.create(event)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    // Review fix: changed from PUT to PATCH since update uses partial merge semantics
    @PatchMapping("/{id}")
    fun patchEvent(@PathVariable id: String, @RequestBody event: Event): ResponseEntity<Event> {
        val updated = eventService.update(id, event)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable id: String): ResponseEntity<Void> {
        if (!eventService.delete(id)) {
            throw EventNotFoundException("Event not found: $id")
        }
        return ResponseEntity.noContent().build()
    }

    // Review fix: wrap date parsing to produce 400 instead of 500
    private fun parseDate(value: String, paramName: String): LocalDate {
        return try {
            LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            throw ApplicationBadRequestException("Invalid date format for '$paramName': $value")
        }
    }
}
