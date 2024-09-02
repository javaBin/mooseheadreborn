package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.admin.*
import no.java.mooseheadreborn.dto.entryregistration.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.*
import java.util.UUID
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Unknown

@Service
class AdminService(
    private val adminRepository: AdminRepository,
    private val workshopRepository: WorkshopRepository,
    private val registrationRepository: RegistrationRepository,
    private val participantRepository: ParticipantRepository,
    private val registrationService: RegistrationService,
    private val reportRepository: ReportRepository,
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
        val registrationList:List<RegistrationRecord> = registrationRepository.allRegistrations()
        val participantList = participantRepository.allParticipants()

        val workshopDtoList:MutableList<AdminWorkshopDto> = mutableListOf()
        val now = Instant.now()

        for (workshopRecord in workshopList) {
            val registrationThisWorkshopList = registrationList
                .filter { it.workshop == workshopRecord.id }
            val adminWorkshopDto = readAdminWorkshop(registrationThisWorkshopList,participantList,workshopRecord,now)
            workshopDtoList.add(adminWorkshopDto)
        }
        return Either.Left(AdminWorkshopSummaryDto(workshopDtoList))
    }

    fun allWorkshopsForEntryRegistration(key: String):Either<AllWorkshopsDto,String> {
        if (!keyIsValid(key)) {
            return Either.Right("No access");
        }
        val workshopList = workshopRepository.allWorkshops()
        val registrationList:List<RegistrationRecord> = registrationRepository.allRegistrations()
        val participantList = participantRepository.allParticipants()

        val mapped:MutableMap<String,MutableList<WorkshopEntryInfoDto>> = mutableMapOf()

        val now = Instant.now()

        for (workshopRecord in workshopList) {
            val registrationThisWorkshopList = registrationList
                .filter { it.workshop == workshopRecord.id }
            val adminWorkshopDto = readAdminWorkshop(registrationThisWorkshopList, participantList, workshopRecord, now)
            val workshopEntryInfoDto = WorkshopEntryInfoDto(
                workshopid = adminWorkshopDto.id,
                workshopName = adminWorkshopDto.name,
                numRegistred = adminWorkshopDto.seatsTaken,
                numWaiting = adminWorkshopDto.waitingSize,
            )
            val slotName:String = when {
                workshopRecord.workshopType == WorkshopType.KIDS.name -> "KIDS"
                workshopRecord.starttime != null -> workshopRecord.starttime.atZoneSameInstant(zoneId).toLocalDateTime().format(shortFormatter)
                else -> "No start time"
            }
            val workshopEntryList = mapped.computeIfAbsent(slotName) { mutableListOf() }
            workshopEntryList.add(workshopEntryInfoDto)
        }

        val slotList:List<WorkshopEntrySlotDto> = mapped.entries.map { entry -> WorkshopEntrySlotDto(entry.key,entry.value) }
        return Either.Left(AllWorkshopsDto(slotList))

    }


    fun changeCapacity(changeCapacityDto: ChangeCapacityDto):Either<AdminWorkshopDto,String> {
        if (!keyIsValid(changeCapacityDto.accessToken)) {
            return Either.Right("No access")
        }
        if (changeCapacityDto.capacity <= 0) {
            return Either.Right("Capacity must be greater than zero")
        }
        val workshopRecord: WorkshopRecord = workshopRepository.workshopFromId(changeCapacityDto.workshopId)?:return Either.Right("Unknown workshop ${changeCapacityDto.workshopId}")
        if (workshopRecord.capacity == changeCapacityDto.capacity) {
            return Either.Right("Capacity is already ${changeCapacityDto.capacity}")
        }
        if (changeCapacityDto.capacity < workshopRecord.capacity) {
            val registrationList = registrationRepository.registrationListForWorkshop(changeCapacityDto.workshopId)
            val numberRegistered =
                registrationList.filter { it.status != RegistrationStatus.CANCELLED.name }.sumOf { it.participantCount }
            if (changeCapacityDto.capacity < numberRegistered) {
                return Either.Right("There are $numberRegistered participants. Can not reduce capacity to ${changeCapacityDto.capacity}")
            }
        } else {
            workshopRecord.capacity = changeCapacityDto.capacity
            registrationService.insertFromWaitingList(workshopRecord)
        }
        workshopRepository.updateCapacity(changeCapacityDto.workshopId,changeCapacityDto.capacity)

        val adminWorkshopDto = readAdminWorkshop(
            registrationThisWorkshopList = registrationRepository.registrationListForWorkshop(changeCapacityDto.workshopId),
            participantList = participantRepository.allParticipants(),
            workshopRecord = workshopRecord,
            now = Instant.now()
        )
        return Either.Left(adminWorkshopDto)
    }

    private fun readAdminWorkshop(registrationThisWorkshopList:List<RegistrationRecord>, participantList:List<ParticiantRecord>, workshopRecord: WorkshopRecord, now:Instant):AdminWorkshopDto {
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
        return adminWorkshopDto
    }

    fun readCollisionSummary(accessKey:String):Either<CollisionSummaryDto,String> {
        if (!keyIsValid(accessKey)) {
            return Either.Right("No access")
        }
        val registrationCollisionList = reportRepository.loadRegistrationCollisionList()
        return Either.Left(CollisionSummaryDto(registrationCollisionList))
    }

    fun readReadEntryRegistrations(accessKey: String,workshopid:String):Either<EntryRegistrationForWorkshopDto,String> {
        if (!keyIsValid(accessKey)) {
            return Either.Right("No access")
        }
        val workshopEntryRegistrationList:List<WorkshopEntryRegistration> = reportRepository.loadEntryRegistration(workshopid)

        return Either.Left(computeEntryList(workshopEntryRegistrationList))
    }

    private fun computeEntryList(workshopEntryRegistrationList:List<WorkshopEntryRegistration>):EntryRegistrationForWorkshopDto {
        val numCheckedIn:Int = workshopEntryRegistrationList.count { it.isCheckedIn }
        return EntryRegistrationForWorkshopDto(workshopEntryRegistrationList,numCheckedIn)
    }

    fun updateCheckin(updateCheckinInputDto: UpdateCheckinInputDto):Either<EntryRegistrationForWorkshopDto,String> {
        if (!keyIsValid(updateCheckinInputDto.accessToken)) {
            return Either.Right("No access")
        }
        val registration:RegistrationRecord = registrationRepository.registrationForId(updateCheckinInputDto.registrationId)?: return Either.Right("Unknown registration ${updateCheckinInputDto.registrationId}")

        val checkedInAt:OffsetDateTime? = if (updateCheckinInputDto.setCheckinTo) OffsetDateTime.now() else null
        if ((checkedInAt == null && registration.checkedInAt != null) ||
            (checkedInAt != null && registration.checkedInAt == null)) {
            registrationRepository.setCheckedInAt(updateCheckinInputDto.registrationId,checkedInAt)
        }
        val workshopEntryRegistrationList:List<WorkshopEntryRegistration> = reportRepository.loadEntryRegistration(registration.workshop)
        return Either.Left(computeEntryList(workshopEntryRegistrationList))
    }

    fun readCheckingForWorkshop(accessKey: String,workshopid:String):Either<EntryRegistrationForWorkshopDto,String> {
        if (!keyIsValid(accessKey)) {
            return Either.Right("No access")
        }
        val workshopEntryRegistrationList:List<WorkshopEntryRegistration> = reportRepository.loadEntryRegistration(workshopid)
        return Either.Left(computeEntryList(workshopEntryRegistrationList))
    }

    companion object {
        private val shortFormatter = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm")
        private val zoneId = ZoneId.of("Europe/Oslo")
    }
}
