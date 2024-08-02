package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.jooq.public_.tables.records.WorkshopRecord
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

@Service
class WorkshopService(
    private val workshopRepository: WorkshopRepository,
    private val timeService: MyTimeService,
    private val adminService: AdminService,
) {
    fun allWorkshops(): List<WorkshopDto> {
        val workshopRecords = workshopRepository.allWorkshops()
        val now:Instant = timeService.currentTime()
        val workshopDtos = workshopRecords.map { WorkshopDto.toDto(it,now) }
        return workshopDtos
    }

    fun workshopById(workshopId:String): WorkshopDto? {
        val workshopRecord = workshopRepository.workshopFromId(workshopId)?:return null

        val now:Instant = timeService.currentTime()
        return WorkshopDto.toDto(workshopRecord,now)
    }



    fun addWorkshop(addWorkshopDto: AddWorkshopDto): Either<ResultWithId,String> {
        if (!adminService.keyIsValid(addWorkshopDto.accessToken)) {
            return Either.Right("No access")
        }
        val id:String = addWorkshopDto.id?:UUID.randomUUID().toString()
        val workshopType:WorkshopType = WorkshopType.fromString(addWorkshopDto.workshopType)?:
            return Either.Right("Unknown workshop type ${addWorkshopDto.workshopType}")

        if (addWorkshopDto.capacity <= 0) {
            return Either.Right("Capacity must be greater than zero")
        }
        val registrationOpens:OffsetDateTime = addWorkshopDto.registrationOpens?.let {
            DateConverter.toOffset(it) ?: return Either.Right("Invalid date ${it}")
        }?:OffsetDateTime.now()

        val changesLocked:OffsetDateTime? = addWorkshopDto.registrationOpens?.let {
            DateConverter.toOffset(it) ?: return Either.Right("Invalid date ${it}")
        }


        val workshopRecord = WorkshopRecord(
            id,
            addWorkshopDto.name,
            workshopType.name,
            addWorkshopDto.capacity,
            workshopType.registerLimit,
            registrationOpens,
            changesLocked
        )
        workshopRepository.addWorkshop(workshopRecord)
        return Either.Left(ResultWithId(id))
    }
}