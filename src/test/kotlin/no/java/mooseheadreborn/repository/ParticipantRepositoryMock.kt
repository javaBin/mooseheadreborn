package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.jooq.public_.tables.records.*
import java.time.*

class ParticipantRepositoryMock:ParticipantRepository {
    val participantStore:MutableList<ParticiantRecord> = mutableListOf()
    val partRegistrationStore:MutableList<ParticipantRegistrationRecord> = mutableListOf()

    override fun addParticipantRegistration(participantRegistrationRecord: ParticipantRegistrationRecord) {
        partRegistrationStore.add(participantRegistrationRecord)
    }

    override fun addParticipant(particiantRecord: ParticiantRecord): ParticiantRecord {
        participantStore.add(particiantRecord)
        return particiantRecord
    }

    override fun participantRegistrationByParticipantId(participantId: String): List<ParticipantRegistrationRecord> =
        partRegistrationStore.filter { it.particiapantId == participantId }

    override fun participantRegistrationByRegisterKey(registerKey: String): ParticipantRegistrationRecord? = partRegistrationStore.firstOrNull { it.registerToken == registerKey }

    override fun participantByEmail(email: String): ParticiantRecord? = participantStore.firstOrNull { it.email == email }

    override fun participantByAccessKey(accessKey: String): ParticiantRecord? = participantStore.firstOrNull { it.accessKey == accessKey }

    override fun participantById(participantId: String): ParticiantRecord? = participantStore.firstOrNull { it.id == participantId }

    override fun setActive(id: String) {
        val particiantRecord:ParticiantRecord = participantStore.firstOrNull { it.id == id }?:return
        particiantRecord.activatedAt = OffsetDateTime.now()
    }

    override fun allParticipants(): List<ParticiantRecord> = participantStore

    override fun updateAccessKey(id: String, accessKey: String) {
        val particiantRecord:ParticiantRecord = participantStore.firstOrNull { it.id == id }?:return
        particiantRecord.accessKey = accessKey
    }

    override fun setParticipantRegistrationUsed(participantRegistrationRecordId: String) {
        val participantRegistrationRecord:ParticipantRegistrationRecord = partRegistrationStore.firstOrNull { it.id == participantRegistrationRecordId }?:return
        participantRegistrationRecord.usedAt = OffsetDateTime.now()
    }
}