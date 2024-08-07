package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.*
import java.util.UUID

@Service
class ParticipantService(
    private val participantRepository: ParticipantRepository,
    private val adminService: AdminService,
    private val sendMailService: SendMailService,
) {
    private fun isValidEmail(email: String):Boolean {
        val atPos = email.indexOf("@")
        return !(atPos < 1 || atPos > email.length-2)
    }
    fun registerParticipant(name:String, email:String):Either<NoDataDto,String> {
        if (!isValidEmail(email)) {
            return Either.Right("Invalid email")
        }
        if (name.trim().isEmpty()) {
            return Either.Right("Name is empty")
        }
        val participant:ParticiantRecord = participantRepository.participantByEmail(email)?:participantRepository.addParticipant(ParticiantRecord(
            UUID.randomUUID().toString(),
            email,
            name,
            UUID.randomUUID().toString(),
            null
        ))


        /*
        if (participant.activatedAt != null) {
            // Send nytt
            return Either.Right("Participant is already activated")
        }*/

        val exsistingList:List<ParticipantRegistrationRecord> = participantRepository.participantRegistrationByParticipantId(participant.id)

        if (exsistingList.any { it.usedAt == null}) {
            return Either.Right("You are already registered. Check your mail and click the activation link")
        }

        val participantRegistrationRecord = ParticipantRegistrationRecord(
            UUID.randomUUID().toString(),
            participant.id,
            UUID.randomUUID().toString(),
            OffsetDateTime.now(),
            null,
        )

        participantRepository.addParticipantRegistration(participantRegistrationRecord)

        sendMailService.sendEmail(email,EmailTemplate.PARTICIPANT_CONFIRMATION, mapOf(EmailVariable.CONFIRM_EMAIL_LINK to EmailTextGenerator.emailConfirnmAddress(participantRegistrationRecord.registerToken)))

        return Either.Left(NoDataDto())
    }

    fun activateParticipant(registerKey:String):Either<UserDto,String> {
        val pr:ParticipantRegistrationRecord = participantRepository.participantRegistrationByRegisterKey(registerKey)?: return Either.Right("Unknown key")
        if (pr.usedAt != null) {
            return Either.Right("Unknown key")
        }

        val particiantRecord:ParticiantRecord = participantRepository.participantById(pr.particiapantId)?:return Either.Right("Unknown access key")

        participantRepository.setParticipantRegistrationUsed(pr.id)


        val accessToken = if (particiantRecord.activatedAt != null) {
          val newAccessToken = UUID.randomUUID().toString()
          participantRepository.updateAccessKey(particiantRecord.id,newAccessToken)
          newAccessToken
        } else {
          participantRepository.setActive(particiantRecord.id)
          particiantRecord.accessKey
        }
        val userDto = UserDto(
            accessToken = accessToken,
            name = particiantRecord.name,
            email = particiantRecord.email,
            userType = UserType.USER
        )

        return Either.Left(userDto)
    }

    fun userFromAccessToken(accessToken:String):UserDto {
        val participant: ParticiantRecord? = participantRepository.participantByAccessKey(accessToken)
        return if (participant != null) {
            UserDto(
                accessToken = participant.accessKey,
                name = participant.name,
                email = participant.email,
                userType = UserType.USER
            )
        } else if (adminService.keyIsValid(accessToken)) {
            return UserDto(
                accessToken = accessToken,
                name = "ADMIN",
                email = "program@java.no",
                userType = UserType.ADMIN
            )
        } else {
             UserDto(
                accessToken = null,
                name = null,
                email = null,
                userType = UserType.ANONYMOUS
            )
        }

    }


}