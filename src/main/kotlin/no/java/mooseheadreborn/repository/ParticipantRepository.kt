package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.Particiant
import no.java.mooseheadreborn.jooq.public_.tables.ParticipantRegistration
import no.java.mooseheadreborn.jooq.public_.tables.records.ParticiantRecord
import org.jooq.*
import org.springframework.stereotype.Repository
import java.time.*
import no.java.mooseheadreborn.jooq.public_.tables.records.ParticipantRegistrationRecord

interface ParticipantRepository {
    fun addParticipantRegistration(participantRegistrationRecord: ParticipantRegistrationRecord)
    fun addParticipant(particiantRecord: ParticiantRecord): ParticiantRecord
    fun participantRegistrationByParticipantId(participantId: String): List<ParticipantRegistrationRecord>
    fun participantRegistrationByRegisterKey(registerKey: String): ParticipantRegistrationRecord?
    fun participantByEmail(email: String): ParticiantRecord?
    fun participantByAccessKey(accessKey: String): ParticiantRecord?
    fun participantById(participantId: String): ParticiantRecord?
    fun setActive(id: String)
    fun allParticipants(): List<ParticiantRecord>

}

@Repository
class ParticipantRepositoryImpl (
    private val dslContext: DSLContext
): ParticipantRepository {
    override fun addParticipantRegistration(participantRegistrationRecord:ParticipantRegistrationRecord) {
        dslContext.executeInsert(participantRegistrationRecord)
    }

    override fun addParticipant(particiantRecord:ParticiantRecord):ParticiantRecord {
        dslContext.executeInsert(particiantRecord)
        return particiantRecord
    }

    override fun participantRegistrationByParticipantId(participantId: String):List<ParticipantRegistrationRecord> {
        return dslContext
            .selectFrom(Tables.PARTICIPANT_REGISTRATION)
            .where(ParticipantRegistration.PARTICIPANT_REGISTRATION.PARTICIAPANT_ID.eq(participantId))
            .fetch()
    }

    override fun participantRegistrationByRegisterKey(registerKey:String):ParticipantRegistrationRecord? {
        return dslContext
            .selectFrom(Tables.PARTICIPANT_REGISTRATION)
            .where(ParticipantRegistration.PARTICIPANT_REGISTRATION.REGISTER_TOKEN.eq(registerKey))
            .fetchOne()
    }

    override fun participantByEmail(email: String): ParticiantRecord? {
        return dslContext.selectFrom(Tables.PARTICIANT)
            .where(Particiant.PARTICIANT.EMAIL.eq(email))
            .fetchOne()
    }

    override fun participantByAccessKey(accessKey: String): ParticiantRecord? {
        return dslContext.selectFrom(Tables.PARTICIANT)
            .where(Particiant.PARTICIANT.ACCESS_KEY.eq(accessKey))
            .fetchOne()
    }

    override fun participantById(participantId: String): ParticiantRecord? {
        return dslContext.selectFrom(Tables.PARTICIANT)
            .where(Particiant.PARTICIANT.ID.eq(participantId))
            .fetchOne()
    }


    override fun setActive(id:String) {
        dslContext.update(Tables.PARTICIANT)
            .set(Particiant.PARTICIANT.ACTIVATED_AT, OffsetDateTime.now())
            .where(Particiant.PARTICIANT.ID.eq(id))
            .execute()
    }

    override fun allParticipants(): List<ParticiantRecord> {
        return dslContext.selectFrom(Tables.PARTICIANT).fetch()
    }


}