package com.coderabbit.calendar

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs

@Service
class WeatherService(
    @Value("\${weather.api-key}") private val apiKey: String,
    @Value("\${weather.city.lat}") private val lat: Double,
    @Value("\${weather.city.lon}") private val lon: Double
) {

    private val logger = LoggerFactory.getLogger(WeatherService::class.java)
    private val restClient = RestClient.create()
    private val timezone = ZoneId.of("Europe/Warsaw")

    private var cachedForecasts: Map<LocalDate, WeatherInfo> = emptyMap()
    private var cacheTimestamp: Instant = Instant.EPOCH
    private val cacheDurationSeconds: Long = 1800 // 30 minutes

    fun getForecasts(): Map<LocalDate, WeatherInfo> {
        val now = Instant.now()
        if (cachedForecasts.isNotEmpty() &&
            now.epochSecond - cacheTimestamp.epochSecond < cacheDurationSeconds
        ) {
            return cachedForecasts
        }
        return try {
            val url = "https://api.openweathermap.org/data/2.5/forecast" +
                "?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

            val response = restClient.get()
                .uri(url)
                .retrieve()
                .body(JsonNode::class.java)

            val forecasts = parseForecasts(response)
            cachedForecasts = forecasts
            cacheTimestamp = now
            forecasts
        } catch (e: Exception) {
            logger.warn("Failed to fetch weather data: ${e.message}")
            cachedForecasts
        }
    }

    fun getWeatherForDate(date: LocalDate): WeatherInfo? = getForecasts()[date]

    private fun parseForecasts(root: JsonNode?): Map<LocalDate, WeatherInfo> {
        if (root == null) return emptyMap()
        val list = root["list"] ?: return emptyMap()

        val grouped = mutableMapOf<LocalDate, MutableList<JsonNode>>()
        for (entry in list) {
            val dt = entry["dt"].asLong()
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), timezone)
            grouped.getOrPut(dateTime.toLocalDate()) { mutableListOf() }.add(entry)
        }

        return grouped.mapValues { (_, entries) ->
            val noonEntry = entries.minByOrNull { entry ->
                val dt = entry["dt"].asLong()
                val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), timezone)
                abs(dateTime.hour - 12)
            }!!

            val main = noonEntry["main"]
            val weather = noonEntry["weather"][0]

            WeatherInfo(
                temperature = main["temp"].asDouble(),
                description = weather["description"].asText(),
                iconCode = weather["icon"].asText()
            )
        }
    }
}
