package com.calendar

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class Event {

    // Review fix: id is read-only from JSON — server always generates it
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: String = UUID.randomUUID().toString()
        private set

    var title: String? = null
    var description: String? = null
    var date: LocalDate? = null
    var startTime: LocalTime? = null
    var endTime: LocalTime? = null
    var color: String = DEFAULT_COLOR

    constructor()

    constructor(
        title: String,
        description: String?,
        date: LocalDate,
        startTime: LocalTime?,
        endTime: LocalTime?,
        color: String?
    ) {
        this.title = title
        this.description = description
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        if (!color.isNullOrBlank()) {
            this.color = color
        }
    }

    /**
     * Regenerate the id with a fresh full UUID.
     * Called by EventService.create to ensure server-controlled ids.
     */
    fun regenerateId() {
        this.id = UUID.randomUUID().toString()
    }

    val timeRange: String
        get() {
            val start = startTime ?: return ""
            val startStr = formatTime(start)
            val end = endTime ?: return startStr
            return "$startStr \u2013 ${formatTime(end)}"
        }

    private fun formatTime(time: LocalTime): String {
        val h = time.hour
        val m = time.minute
        val amPm = if (h < 12) "AM" else "PM"
        val displayHour = if (h % 12 == 0) 12 else h % 12
        return if (m == 0) "$displayHour $amPm"
        else "$displayHour:${"%02d".format(m)} $amPm"
    }

    companion object {
        const val DEFAULT_COLOR = "#1a73e8"
        val COLOR_PATTERN = Regex("^#[0-9a-fA-F]{3}([0-9a-fA-F]{3})?\$")
    }
}
