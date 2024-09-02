package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.enduser.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.*
import java.util.UUID
import kotlin.math.*

@Service
class RegistrationService(
    private val workshopRepository: WorkshopRepository,
    private val participantRepository: ParticipantRepository,
    private val registrationRepository: RegistrationRepository,
    private val timeService: MyTimeService,
    private val sendMailService:SendMailService,
) {
    private val formatter = DateTimeFormatter.ofPattern("LLLL dd' at 'HH:mm")
    private val shortFormatter = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm")
    private val zoneId = ZoneId.of("Europe/Oslo")

    fun addRegistration(accessToken:String,workshopId:String,numParticipants:Int):Either<AddRegistrationResultDto,String> {
        if (numParticipants < 1) {
            return Either.Right("Invalid number of participants")
        }

        val workshop = workshopRepository.workshopFromId(workshopId) ?: return Either.Right("Workshop not found")
        val participant = participantRepository.participantByAccessKey(accessToken)
            ?: return Either.Right("Participant not found")

        if (workshop.registrationOpen.toInstant().isAfter(timeService.currentTime())) {
            return Either.Right("Workshop is not open for registration")
        }

        if (numParticipants > workshop.registerLimit) {
            return Either.Right("Invalid number of participants")
        }
        val registrationStatus = computeRegistrationStatus(workshop,numParticipants)
        val rr = RegistrationRecord(
            UUID.randomUUID().toString(),
            registrationStatus.name,
            workshop.id,
            participant.id,
            numParticipants,
            OffsetDateTime.now(),
            null,
            null,
        )
        registrationRepository.addRegistration(rr)



        sendMailService.sendEmail(
            to = participant.email,
            emailTemplate = if (registrationStatus == RegistrationStatus.WAITING) EmailTemplate.REGISTER_CONFIRMATION_WAITING else EmailTemplate.REGISTER_CONFIRMATION,
            variableMap = mapOf(
                    EmailVariable.CANCEL_LINK to EmailTextGenerator.cancelLinkAddress(rr.id),
                    EmailVariable.WORKSHOP_NAME to workshop.name,
                    EmailVariable.WORKSHOP_TIME_TEXT to workshopStartText(workshop.starttime),
                    EmailVariable.PARTICIPANT_REGISTER_LINK to EmailTextGenerator.participantSummmaryAddress(participant.id)
                )
        )

        return Either.Left(AddRegistrationResultDto(
            registrationStatus = registrationStatus,
            registrationId = rr.id
        ))

    }

    private fun workshopStartText(starttime:OffsetDateTime?):String {
        if (starttime == null) {
            return ""
        }
        val formattedStartDate = starttime.format(formatter)
        return " The workshop starts $formattedStartDate."

    }

    fun insertFromWaitingList(workshop:WorkshopRecord) {
        if (workshop.changesLocked != null && timeService.currentTime().isAfter(workshop.changesLocked.toInstant())) {
            return
        }
        val registrationList = registrationRepository.registrationListForWorkshop(workshop.id)
            .filter { it.cancelledAt == null }
            .sortedBy { it.registeredAt }

        var numRegistered = 0
        for (registration in registrationList) {
            numRegistered+=registration.participantCount
            if (numRegistered > workshop.capacity) {
                break
            }
            if (registration.status == RegistrationStatus.WAITING.name) {
                val participant: ParticiantRecord? = participantRepository.participantById(registration.participant)
                if (participant != null) {
                    registrationRepository.updateRegistrationStatus(registration.id, RegistrationStatus.REGISTERED)
                    sendMailService.sendEmail(
                        to = participant.email,
                        emailTemplate = EmailTemplate.REGISTER_CONFIRMATION,
                        variableMap = mapOf(
                            EmailVariable.CANCEL_LINK to EmailTextGenerator.cancelLinkAddress(registration.id),
                            EmailVariable.WORKSHOP_NAME to workshop.name,
                            EmailVariable.WORKSHOP_TIME_TEXT to workshopStartText(workshop.starttime),
                            EmailVariable.PARTICIPANT_REGISTER_LINK to EmailTextGenerator.participantSummmaryAddress(participant.id),
                        )
                    )
                }
            }
        }
    }

    fun cancelRegistration(registrationId:String,accessToken: String?):Either<CancelRegistrationResultDto,String> {
        val registation =  registrationRepository.registrationForId(registrationId)
            ?:return Either.Right("Registration not found")
        if (registation.cancelledAt != null) {
            return Either.Right("Registration already cancelled")
        }
        val workshop:WorkshopRecord = workshopRepository.workshopFromId(registation.workshop)
            ?: return Either.Right("Workshop not found ${registation.workshop}")
        registrationRepository.cancelRegistration(registrationId)


        val particiantRecordFromWorkshop:ParticiantRecord? = participantRepository.participantById(registation.participant)
        if (particiantRecordFromWorkshop != null) {
            sendMailService.sendEmail(particiantRecordFromWorkshop.email,EmailTemplate.CANCEL_CONFIRMATION, mapOf(
                EmailVariable.WORKSHOP_NAME to workshop.name,
                EmailVariable.PARTICIPANT_REGISTER_LINK to EmailTextGenerator.participantSummmaryAddress(registation.participant),
            ))

        }

        insertFromWaitingList(workshop)


        val particiantRecord:ParticiantRecord? = accessToken?.let { participantRepository.participantByAccessKey(it) }
        val registrationStatus:RegistrationStatus = if (particiantRecord != null) RegistrationStatus.NOT_REGISTERED else RegistrationStatus.NOT_LOGGED_IN
        return Either.Left(CancelRegistrationResultDto(registrationStatus))
    }

    private fun computeRegistrationStatus(workshopRecord: WorkshopRecord,numParticipants: Int):RegistrationStatus {
        val registrationList = registrationRepository.registrationListForWorkshop(workshopRecord.id)
        val numRegistered = registrationList.filter { it.cancelledAt == null }.sumOf { it.participantCount }
        return if (numRegistered+numParticipants > workshopRecord.capacity) RegistrationStatus.WAITING else RegistrationStatus.REGISTERED
    }

    fun readParticipantDto(accessKey:String):Either<ParticipantDto,String> {
        val particiant:ParticiantRecord = participantRepository.participantByAccessKey(accessKey)
            ?:return Either.Right("Unknown access key")
        val registrationList:List<RegistrationRecord> = registrationRepository.registationListByParticipant(particiant.id)

        val participationList:List<UserParticipationDto> = registrationList
            .filter { it.cancelledAt == null }
            .sortedBy { it.registeredAt }
            .map { rr ->
                val workshopRecord = workshopRepository.workshopFromId(rr.workshop)
                val workshopName:String = workshopRecord?.name?:"Unknown name"
                val registrationStatus:RegistrationStatus = RegistrationStatus.valueOf(rr.status)
                UserParticipationDto(
                    workshopName = workshopName,
                    participationStatus = registrationStatus.displayText,
                    participantCount = if ((workshopRecord?.registerLimit?:1) > 1) rr.participantCount else null
                )
            }
        return Either.Left(
            ParticipantDto(
                name = particiant.name,
                email = particiant.email,
                participationList = participationList
            )
        )
    }

    private fun readUserRegistration(workshopId: String,accessToken: String?):Either<Triple<RegistrationStatus,String?,Int?>,String> {
        if (accessToken == null) {
            return Either.Left(Triple(RegistrationStatus.NOT_LOGGED_IN,null,null))
        }
        val particiantRecord:ParticiantRecord = participantRepository.participantByAccessKey(accessToken)?:return Either.Right("Unknown accessToken")
        val registrationRecordList = registrationRepository.registationListByParticipant(particiantRecord.id)
        val registrationRecord:RegistrationRecord? = registrationRecordList.firstOrNull { it.workshop == workshopId && it.cancelledAt == null}?:
            registrationRecordList.firstOrNull { it.workshop == workshopId }
        if (registrationRecord == null) {
            return Either.Left(Triple(RegistrationStatus.NOT_REGISTERED,null,null))
        }
        if (registrationRecord.cancelledAt != null) {
            return Either.Left(Triple(RegistrationStatus.CANCELLED, null,null))
        }
        val registrationStatus = RegistrationStatus.valueOf(registrationRecord.status)
        return Either.Left(Triple(registrationStatus,registrationRecord.id,registrationRecord.participantCount))
    }



    fun participantInfoForWorkshop(workshopId: String,accessToken: String?):Either<UserWorkshopRegistrationDto,String> {
        val workshopRecord = workshopRepository.workshopFromId(workshopId)?:return Either.Right("Unknown workshopid")
        val registrationCount = registrationRepository.totalRegistrationsOnWorkshop(workshopId)
        val workshopDto:WorkshopDto = WorkshopDto.toDto(workshopRecord, Instant.now(),registrationCount)
        val userRegistration = readUserRegistration(workshopId,accessToken)
        return userRegistration.fold(
            left = { (registrationStatus: RegistrationStatus, registrationId: String?,numRegistrations:Int?) ->
                Either.Left(
                    UserWorkshopRegistrationDto(
                        workshop = workshopDto,
                        registrationStatus = registrationStatus,
                        registrationStatusText = registrationStatus.displayText,
                        registrationId = registrationId,
                        numRegistered = if (workshopRecord.registerLimit > 1) numRegistrations else null
                    )
                )
            },
            right = {errormessage:String -> Either.Right(errormessage)}
        )
    }

    fun participantInfoFromRegistrationId(registrationId:String):Either<UserWorkshopRegistrationDto,String> {
        val rrFromId:RegistrationRecord = registrationRepository.registrationForId(registrationId)?:return Either.Right("Unknown registrationid")
        val registrationRecord:RegistrationRecord = if (rrFromId.cancelledAt != null) {
            val registrationRecordList = registrationRepository.registationListByParticipant(rrFromId.participant)
            registrationRecordList.firstOrNull { it.workshop == rrFromId.workshop && it.cancelledAt == null }?:rrFromId
        } else rrFromId
        val workshopRecord:WorkshopRecord = workshopRepository.workshopFromId(registrationRecord.workshop)?:return Either.Right("Unknown workshopid")
        val registrationCount = registrationRepository.totalRegistrationsOnWorkshop(workshopRecord.id)
        val workshopDto:WorkshopDto = WorkshopDto.toDto(workshopRecord, Instant.now(),registrationCount)
        val registrationStatus = RegistrationStatus.valueOf(registrationRecord.status)
        val userWorkshopRegistrationDto = UserWorkshopRegistrationDto(
            workshop = workshopDto,
            registrationStatus = registrationStatus,
            registrationStatusText = registrationStatus.displayText,
            registrationId = registrationRecord.id,
            numRegistered = if (workshopRecord.registerLimit > 1) registrationRecord.participantCount else null
        )
        return Either.Left(userWorkshopRegistrationDto)
    }

    fun participantInfoForParticipantId(participantId:String):Either<ParticipantRegistrationsDto,String> {
        val participantRecord: ParticiantRecord = participantRepository.participantById(participantId)?:return Either.Right("Unknown participantid")
        val registrationList:List<RegistrationRecord> = registrationRepository.registationListByParticipant(participantId)

        if (registrationList.isEmpty()) {
            return Either.Left(ParticipantRegistrationsDto(participantRecord.name, emptyList()))
        }

        val workshopList:List<WorkshopRecord> = workshopRepository.allWorkshops()
        val allGrouped:Map<String,List<RegistrationRecord>> = registrationList.groupBy { it.workshop }

        val registrationInfoList:List<RegistrationInfoDto> = allGrouped.mapNotNull { entry ->
            val rr:RegistrationRecord? = entry.value.maxByOrNull { it.registeredAt }
            val wr:WorkshopRecord? = workshopList.firstOrNull { it.id == entry.key }
            if (rr != null && wr != null) {
                val registrationStatus = RegistrationStatus.valueOf(rr.status)
                RegistrationInfoDto(
                    workshopName = wr.name,
                    registrationStatus = registrationStatus,
                    registrationStatusText = registrationStatus.displayText,
                    startTime = formatOffset(wr.starttime),
                    endTime = formatOffset(wr.endtime),
                )
            } else {
                null
            }
        }
        return Either.Left(ParticipantRegistrationsDto(participantRecord.name, registrationInfoList))

    }

    private fun formatOffset(offsetDateTime: OffsetDateTime?):String? {
        if (offsetDateTime == null) {
            return null
        }
        return offsetDateTime.atZoneSameInstant(zoneId).toLocalDateTime().format(shortFormatter)
    }

}