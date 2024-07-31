package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.Particiant
import no.java.mooseheadreborn.jooq.public_.tables.ParticipantRegistration
import no.java.mooseheadreborn.jooq.public_.tables.records.ParticiantRecord
import org.jooq.*
import org.springframework.stereotype.Repository
import java.time.*
import no.java.mooseheadreborn.jooq.public_.tables.records.ParticipantRegistrationRecord

@Repository
class ParticipantRepository(
    private val dslContext: DSLContext
) {
    fun addParticipantRegistration(participantRegistrationRecord:ParticipantRegistrationRecord) {
        dslContext.executeInsert(participantRegistrationRecord)
    }

    fun addParticipant(particiantRecord:ParticiantRecord):ParticiantRecord {
        dslContext.executeInsert(particiantRecord)
        return particiantRecord
    }

    fun participantRegistrationByParticipantId(participantId: String):List<ParticipantRegistrationRecord> {
        return dslContext
            .selectFrom(Tables.PARTICIPANT_REGISTRATION)
            .where(ParticipantRegistration.PARTICIPANT_REGISTRATION.PARTICIAPANT_ID.eq(participantId))
            .fetch()
    }

    fun participantRegistrationByRegisterKey(registerKey:String):ParticipantRegistrationRecord? {
        return dslContext
            .selectFrom(Tables.PARTICIPANT_REGISTRATION)
            .where(ParticipantRegistration.PARTICIPANT_REGISTRATION.REGISTER_TOKEN.eq(registerKey))
            .fetchOne()
    }

    fun participantByEmail(email: String): ParticiantRecord? {
        return dslContext.selectFrom(Tables.PARTICIANT)
            .where(Particiant.PARTICIANT.EMAIL.eq(email))
            .fetchOne()
    }

    fun participantByAccessKey(accessKey: String): ParticiantRecord? {
        return dslContext.selectFrom(Tables.PARTICIANT)
            .where(Particiant.PARTICIANT.ACCESS_KEY.eq(accessKey))
            .fetchOne()
    }

    fun participantById(participantId: String): ParticiantRecord? {
        return dslContext.selectFrom(Tables.PARTICIANT)
            .where(Particiant.PARTICIANT.ID.eq(participantId))
            .fetchOne()
    }


    fun setActive(id:String) {
        dslContext.update(Tables.PARTICIANT)
            .set(Particiant.PARTICIANT.ACTIVATED_AT, OffsetDateTime.now())
            .where(Particiant.PARTICIANT.ID.eq(id))
            .execute()
    }

    fun allParticipants(): List<ParticiantRecord> {
        return dslContext.selectFrom(Tables.PARTICIANT).fetch()
    }


}