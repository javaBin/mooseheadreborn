package no.java.mooseheadreborn.service

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

    fun activateParticipant(registerKey:String):Either<ParticipantActivationDto,String> {
        val pr = participantRepository.participantRegistrationByRegisterKey(registerKey)?: return Either.Right("Unknown key")

      val particiantRecord:ParticiantRecord = participantRepository.participantById(pr.particiapantId)?:return Either.Right("Unknown access key")
      if (particiantRecord.activatedAt != null) {
          return Either.Right("Participant is already activated")
      }
      participantRepository.setActive(particiantRecord.id)
      return Either.Left(ParticipantActivationDto(particiantRecord.accessKey))
    }


}