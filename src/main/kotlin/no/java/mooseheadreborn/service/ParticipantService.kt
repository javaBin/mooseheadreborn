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
class ParticipantService(
    private val participantRepository: ParticipantRepository,
    private val sendMailService: SendMailService,
) {
    fun registerParticipant(name:String, email:String):Either<NoDataDto,String> {
        val participant:ParticiantRecord = participantRepository.participantByEmail(email)?:participantRepository.addParticipant(ParticiantRecord(
            UUID.randomUUID().toString(),
            email,
            name,
            UUID.randomUUID().toString(),
            null
        ))

        if (participant.activatedAt != null) {
            return Either.Right("Participant is already activated")
        }

        val exsisting = participantRepository.participantRegistrationByParticipantId(participant.id)

        if (exsisting.isNotEmpty()) {
            return Either.Right("Participant is already registered")
        }

        val participantRegistrationRecord = ParticipantRegistrationRecord(
            UUID.randomUUID().toString(),
            participant.id,
            UUID.randomUUID().toString(),
            OffsetDateTime.now(),
            null,
        )

        participantRepository.addParticipantRegistration(participantRegistrationRecord)

        sendMailService.sendEmail(email,"Confirm email",
            EmailTextGenerator.loadText(EmailTemplate.PARTICIPANT_CONFIRMATION, mapOf(EmailVariable.REGISTER_KEY to participantRegistrationRecord.registerToken)))

        return Either.Left(NoDataDto())
    }

    fun activateParticipant(registerKey:String):Either<UserDto,String> {
        val pr = participantRepository.participantRegistrationByRegisterKey(registerKey)?: return Either.Right("Unknown key")

      val particiantRecord:ParticiantRecord = participantRepository.participantById(pr.particiapantId)?:return Either.Right("Unknown access key")
        val userDto = UserDto(
            accessToken = particiantRecord.accessKey,
            name = particiantRecord.name,
            email = particiantRecord.email,
            userType = UserType.USER
        )
      if (particiantRecord.activatedAt != null) {
          //return Either.Right("Participant is already activated")
          return Either.Left(userDto)
      }
      participantRepository.setActive(particiantRecord.id)
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