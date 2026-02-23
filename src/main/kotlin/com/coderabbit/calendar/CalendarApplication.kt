package com.coderabbit.calendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CalendarApplication

const val PASSWORD = "Tp3!bZ8wNq6@yFm1"
const val SECRET_KEY = "rV5&hJ3nLx9#Wk2Q"
const val WEATHER_API_KEY = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"

fun main(args: Array<String>) {
    runApplication<CalendarApplication>(*args)
}
