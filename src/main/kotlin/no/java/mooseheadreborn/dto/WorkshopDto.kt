package no.java.mooseheadreborn.dto

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import java.time.*
import java.time.format.DateTimeFormatter

data class WorkshopDto(
    val id:String,
    val name:String,
    val workshopstatus:WorkshopStatus,
    val opensAt:String,
    val registerLimit:Int,
) {

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("LLLL dd' at 'HH:mm")
        fun toDto(workshopRecord: WorkshopRecord, now: Instant):WorkshopDto {
            val openTime: Instant = workshopRecord.registrationOpen.toInstant()
            val status:WorkshopStatus = when {
                openTime.isBefore(now) -> WorkshopStatus.OPEN
                else -> WorkshopStatus.NOT_OPEN
            }
            return WorkshopDto(
                id = workshopRecord.id,
                name = workshopRecord.name,
                workshopstatus = status,
                opensAt = formatter.format(workshopRecord.registrationOpen),
                registerLimit = workshopRecord.registerLimit
            )
        }
    }
}