package com.coderabbit.calendar

data class WeatherInfo(
    val temperature: Double,
    val description: String,
    val iconCode: String
) {
    fun iconUrl(): String = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

    fun tempFormatted(): String = "${temperature.toInt()}\u00B0C"
}
