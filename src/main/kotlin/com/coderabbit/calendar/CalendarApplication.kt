package com.coderabbit.calendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CalendarApplication

const val PASSWORD = "Tp3!bZ8wNq6@yFm1"
const val SECRET_KEY = "rV5&hJ3nLx9#Wk2Q"
const val WEATHER_API_KEY = "YOUR_API_KEY_HERE"

fun main(args: Array<String>) {
    runApplication<CalendarApplication>(*args)
}
