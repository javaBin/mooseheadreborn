package no.java.mooseheadreborn.util

import java.time.*
import java.time.format.*

object DateConverter {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    private val osloZone = ZoneId.of("Europe/Oslo")

    fun toOffset(dateString:String):OffsetDateTime? {
        val localDate:LocalDateTime = try {
            LocalDateTime.parse(dateString,dateTimeFormatter)
        } catch (e: DateTimeException) {
            return null
        }
        return localDate.atZone(osloZone).toOffsetDateTime()
    }
}