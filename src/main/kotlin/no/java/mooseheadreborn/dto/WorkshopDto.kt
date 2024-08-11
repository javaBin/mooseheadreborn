package no.java.mooseheadreborn.dto

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import java.time.*
import java.time.format.DateTimeFormatter

data class WorkshopDto(
    val id:String,
    val name:String,
    val workshopstatus:WorkshopStatus,
    val workshopStatusText:String,
    val opensAt:String,
    val registerLimit:Int,
    val workshopType: WorkshopType

) {

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("LLLL dd' at 'HH:mm")
        fun toDto(workshopRecord: WorkshopRecord, now: Instant,numSpotsReserved:Int?):WorkshopDto {

            val status:WorkshopStatus = toStatus(workshopRecord,now,numSpotsReserved)
            val opensAt = formatter.format(workshopRecord.registrationOpen)
            return WorkshopDto(
                id = workshopRecord.id,
                name = workshopRecord.name,
                workshopType = WorkshopType.valueOf(workshopRecord.workshopType),
                workshopstatus = status,
                opensAt = opensAt,
                registerLimit = workshopRecord.registerLimit,
                workshopStatusText = if (status == WorkshopStatus.NOT_OPEN) "${status.text} (Opens $opensAt)" else status.text
            )
        }

        fun toStatus(workshopRecord: WorkshopRecord,now: Instant,numSpotsReserved: Int?):WorkshopStatus = when {
            workshopRecord.registrationOpen.toInstant().isAfter(now) -> WorkshopStatus.NOT_OPEN
            numSpotsReserved != null && numSpotsReserved >= workshopRecord.registerLimit -> WorkshopStatus.FULL
            else -> WorkshopStatus.OPEN
        }
    }
}