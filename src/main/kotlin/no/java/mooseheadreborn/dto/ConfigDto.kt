package no.java.mooseheadreborn.dto

import java.time.OffsetDateTime
import java.time.OffsetTime

data class ConfigDto(
    val version:String="1.0.0",
    val currentTime:String = OffsetDateTime.now().toString()
    ) {
}