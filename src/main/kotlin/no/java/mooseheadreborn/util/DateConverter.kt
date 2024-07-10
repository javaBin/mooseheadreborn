package no.java.mooseheadreborn.util

import java.time.OffsetDateTime
import java.time.format.*

object DateConverter {
    fun toOffset(dateString:String):OffsetDateTime? {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")
        return try {
            OffsetDateTime.parse(dateString, dateTimeFormatter)

        } catch (e: DateTimeParseException) {
            null
        }
    }
}