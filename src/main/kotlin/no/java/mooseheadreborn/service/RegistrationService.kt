package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.enduser.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.*
import java.util.UUID

@Service
class RegistrationService(
    private val workshopRepository: WorkshopRepository,
    private val participantRepository: ParticipantRepository,
    private val registrationRepository: RegistrationRepository,
    private val timeService: MyTimeService,
    private val sendMailService:SendMailService,
) {
    fun addRegistration(accessToken:String,workshopId:String,numParticipants:Int):Either<RegistrationStatus,String> {
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
            null
        )
        registrationRepository.addRegistration(rr)

        sendMailService.sendEmail(participant.email,"Workshop confirmation",
            EmailTextGenerator.loadText(EmailTemplate.REGISTER_CONFIRMATION, mapOf(EmailVariable.REGISTRATION_ID to rr.id))
        )

        return Either.Left(registrationStatus)
    }

    fun cancelRegistration(registrationId:String):Either<NoDataDto,String> {
        val registation =  registrationRepository.registrationForId(registrationId)
            ?:return Either.Right("Registration not found")
        if (registation.cancelledAt != null) {
            return Either.Right("Registration already cancelled")
        }
        val workshop:WorkshopRecord = workshopRepository.workshopFromId(registation.workshop)
            ?: return Either.Right("Workshop not found ${registation.workshop}")
        registrationRepository.cancelRegistration(registrationId)

        val registrationList = registrationRepository.registrationListForWorkshop(registation.workshop)
            .filter { it.cancelledAt == null }
            .sortedBy { it.registeredAt }

        var numRegistered = 0
        for (registration in registrationList) {
            numRegistered+=registration.participantCount
            if (numRegistered > workshop.registerLimit) {
                break
            }
            if (registration.status == RegistrationStatus.WAITING.name) {
                val participant: ParticiantRecord? = participantRepository.participantById(registration.participant)
                if (participant != null) {
                    registrationRepository.updateRegistrationStatus(registration.id, RegistrationStatus.REGISTERED)
                    sendMailService.sendEmail(
                        participant.email, "Workshop confirmation",
                        EmailTextGenerator.loadText(
                            EmailTemplate.REGISTER_CONFIRMATION,
                            mapOf(EmailVariable.REGISTRATION_ID to registration.id)
                        )
                    )
                }
            }
        }

        return Either.Left(NoDataDto())
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
}