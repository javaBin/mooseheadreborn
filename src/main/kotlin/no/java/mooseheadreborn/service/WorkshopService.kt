package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.admin.*
import no.java.mooseheadreborn.jooq.public_.tables.records.WorkshopRecord
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.*
import java.util.UUID

@Service
class WorkshopService(
    private val workshopRepository: WorkshopRepository,
    private val timeService: MyTimeService,
    private val adminService: AdminService,
    private val readProgramService: ReadProgramService,
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
            changesLocked,
            null,
            null
        )
        workshopRepository.addWorkshop(workshopRecord)
        return Either.Left(ResultWithId(id))
    }

    private fun titleToId(title:String):String {
        val res = StringBuilder()
        for (char in title.lowercase()) {
            if (char < 'a' || char > 'z') {
                continue
            }
            res.append(char)
            if (res.length >= 15) {
                break
            }
        }
        return res.toString()
    }

    fun createWorkshopsFromMoosehead(moresleepCreateWorkshopsDto:MoresleepCreateWorkshopsDto): String? {
        if (!adminService.keyIsValid(moresleepCreateWorkshopsDto.accessToken)) {
            return "No access"
        }
        val moresleepProgram = readProgramService.fetchProgram() ?: return "Could not fetch program from moresleep"
        val opens:OffsetDateTime =  DateConverter.toOffset(moresleepCreateWorkshopsDto.opensAt)?:return "Invalid date ${moresleepCreateWorkshopsDto.opensAt}"
        val zone = ZoneId.of("Europe/Oslo")
        for (moresleepSession in moresleepProgram.sessions) {
            if (moresleepSession.format != "workshop") {
                continue
            }
            val id = titleToId(moresleepSession.title)

            if (workshopRepository.workshopFromId(id) != null) {
                continue
            }

            val startTime:OffsetDateTime? = moresleepSession.startTime?.let { LocalDateTime.parse(it).atZone(zone).toOffsetDateTime() }
            val endTime:OffsetDateTime? = moresleepSession.endTime?.let { LocalDateTime.parse(it).atZone(zone).toOffsetDateTime() }

            val workshopRecord = WorkshopRecord(
                id,
                moresleepSession.title,
                WorkshopType.JZ.name,
                moresleepCreateWorkshopsDto.capacity,
                1,
                opens,
                null,
                startTime,
                endTime
            )
            workshopRepository.addWorkshop(workshopRecord)
        }
        return null;
    }


}