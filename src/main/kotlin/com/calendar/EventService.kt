package com.calendar

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap

@Service
class EventService {

    private val events = ConcurrentHashMap<String, Event>()

    fun create(event: Event): Event {
        if (event.title.isNullOrBlank()) {
            throw ApplicationBadRequestException("Event title is required")
        }
        if (event.date == null) {
            throw ApplicationBadRequestException("Event date is required")
        }
        if (event.startTime != null && event.endTime != null &&
            event.endTime!!.isBefore(event.startTime!!)
        ) {
            throw ApplicationBadRequestException("End time must be after start time")
        }
        validateColor(event.color)

        // Review fix: always regenerate server-controlled id, ignoring any client-supplied value
        event.regenerateId()
        events[event.id] = event
        return event
    }

    fun findById(id: String): Event? = events[id]

    // Review fix: returns immutable list (toList() in Kotlin copies)
    fun findByDate(date: LocalDate): List<Event> =
        events.values
            .filter { it.date == date }
            .sortedBy { it.startTime ?: LocalTime.MAX }
            .toList()

    fun findByDateRange(start: LocalDate, end: LocalDate): Map<LocalDate, List<Event>> =
        events.values
            .filter { it.date != null && !it.date!!.isBefore(start) && !it.date!!.isAfter(end) }
            .groupBy { it.date!! }

    fun findAll(): List<Event> =
        events.values
            .sortedWith(compareBy<Event> { it.date }.thenBy { it.startTime ?: LocalTime.MAX })
            .toList()

    // Review fix: atomic update via compute()
    fun update(id: String, updated: Event): Event {
        val result = events.compute(id) { _, existing ->
            if (existing == null) {
                throw EventNotFoundException("Event not found: $id")
            }
            if (!updated.title.isNullOrBlank()) {
                existing.title = updated.title
            }
            if (updated.description != null) {
                existing.description = updated.description
            }
            if (updated.date != null) {
                existing.date = updated.date
            }
            if (updated.startTime != null) {
                existing.startTime = updated.startTime
            }
            if (updated.endTime != null) {
                existing.endTime = updated.endTime
            }
            if (!updated.color.isNullOrBlank() && updated.color != Event.DEFAULT_COLOR) {
                validateColor(updated.color)
                existing.color = updated.color
            }

            // Validate time invariant after applying all changes
            if (existing.startTime != null && existing.endTime != null &&
                existing.endTime!!.isBefore(existing.startTime!!)
            ) {
                throw ApplicationBadRequestException("End time must be after start time")
            }

            existing
        }
        return result!!
    }

    fun delete(id: String): Boolean = events.remove(id) != null

    fun count(): Int = events.size

    // Review fix: validate color is safe hex pattern to prevent CSS injection
    private fun validateColor(color: String) {
        if (!Event.COLOR_PATTERN.matches(color)) {
            throw ApplicationBadRequestException(
                "Invalid color format: must be a hex color (e.g. #1a73e8 or #fff)"
            )
        }
    }
}
