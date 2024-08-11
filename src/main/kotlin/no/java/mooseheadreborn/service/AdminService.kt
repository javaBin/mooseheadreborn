package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.admin.*
import no.java.mooseheadreborn.jooq.public_.tables.records.AdminKeysRecord
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.*
import java.util.UUID

@Service
class AdminService(
    private val adminRepository: AdminRepository,
    private val workshopRepository: WorkshopRepository,
    private val registrationRepository: RegistrationRepository,
    private val participantRepository: ParticipantRepository
) {
    fun createAccess(password:String):Either<UserDto,String> {
        if (Config.getConfigValue(ConfigVariable.ADMIN_PASSWORD) != password) {
            return Either.Right("No access");
        }
        val key = UUID.randomUUID().toString()
        val created = OffsetDateTime.now()
        val adminKeyRecord = AdminKeysRecord(key, created)
        adminRepository.addKey(adminKeyRecord)
        val userDto = UserDto(key, "ADMIN", "program@java.no", UserType.ADMIN)
        return Either.Left(userDto)
    }

    fun keyIsValid(key:String):Boolean {
        val adminRecord:AdminKeysRecord = adminRepository.readKey(key)?:return false

        return (adminRecord.created.plusMinutes(ConfigVariable.ADMIN_LOGIN_DURATION_MINUTES.longValue()).isAfter(OffsetDateTime.now()))
    }

    fun allRegistration(key:String):Either<AdminWorkshopSummaryDto,String> {
        if (!keyIsValid(key)) {
            return Either.Right("No access");
        }
        val workshopList = workshopRepository.allWorkshops()
        val registrationList = registrationRepository.allRegistrations()
        val participantList = participantRepository.allParticipants()

        val workshopDtoList:MutableList<AdminWorkshopDto> = mutableListOf()
        val now = Instant.now()

        for (workshopRecord in workshopList) {
            val registrationThisWorkshopList = registrationList
                .filter { it.workshop == workshopRecord.id }
            val registratonList = registrationThisWorkshopList
                .map { registration ->
                    val participant = participantList.firstOrNull { it.id == registration.participant }
                    AdminWorkshopRegistration(
                        id = registration.id,
                        status = RegistrationStatus.valueOf(registration.status),
                        name = participant?.name?:"Unknown",
                        email = participant?.email?:"Unknown",
                        numSpots = registration.participantCount,
                        participantId = registration.participant,
                        registeredAt = registration.registeredAt.toString()
                    )
                }.sortedWith(compareBy({ if (it.status == RegistrationStatus.CANCELLED) 1 else 0},{it.registeredAt}),)
            val seatsTaken:Int = registrationThisWorkshopList.filter { it.status == RegistrationStatus.REGISTERED.name }.sumOf { it.participantCount }
            val waitingSize:Int = registrationThisWorkshopList.filter { it.status == RegistrationStatus.WAITING.name }.sumOf { it.participantCount }
            val adminWorkshopDto = AdminWorkshopDto(
                id = workshopRecord.id,
                name = workshopRecord.name,
                workshopType = WorkshopType.valueOf(workshopRecord.workshopType),
                workshopstatus = WorkshopDto.toStatus(workshopRecord,now,seatsTaken+waitingSize),
                opensAt = workshopRecord.registrationOpen.toString(),
                registerLimit = workshopRecord.registerLimit,
                capacity = workshopRecord.capacity,
                registrationList = registratonList,
                seatsTaken = seatsTaken,
                waitingSize = waitingSize,

            )
            workshopDtoList.add(adminWorkshopDto)
        }
        return Either.Left(AdminWorkshopSummaryDto(workshopDtoList))
    }
}
