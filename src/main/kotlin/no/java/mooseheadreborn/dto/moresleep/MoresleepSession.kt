package no.java.mooseheadreborn.dto.moresleep

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MoresleepSession(
    val title:String,
    val format:String,
    val startTime:String?,
    val endTime:String?,
) {
}