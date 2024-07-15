package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ParticipantService(
    private val participantRepository: ParticipantRepository,
    private val sendMailService: SendMailService,
) {
    fun registerParticipant(name:String,email:String):Either<NoDataDto,String> {
        if (participantRepository.participantByEmail(email) != null) {
            return Either.Right("Participant with email already exists")
        }
        val accessKey = UUID.randomUUID()

        val pr = ParticiantRecord(
            UUID.randomUUID().toString(),
            email,
            name,
            accessKey.toString(),
            null
        )

        participantRepository.addParticipant(pr)

        sendMailService.sendEmail(email,"Confirm email",
            EmailTextGenerator.loadText(EmailTemplate.PARTICIPANT_CONFIRMATION, mapOf(EmailVariable.ACCESS_KEY to accessKey.toString())))

        return Either.Left(NoDataDto())
    }

    fun activateParticipant(accessKey:String):Either<NoDataDto,String> {
      val particiantRecord:ParticiantRecord = participantRepository.participantByAccessKey(accessKey)?:return Either.Right("Unknown access key")
      if (particiantRecord.activatedAt != null) {
          return Either.Right("Participant is already activated")
      }
      participantRepository.setActive(particiantRecord.id)
      return Either.Left(NoDataDto())
    }
}