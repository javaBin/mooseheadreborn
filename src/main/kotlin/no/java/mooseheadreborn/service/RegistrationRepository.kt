package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.records.RegistrationRecord
import org.jooq.*
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class RegistrationRepository(
    private val dslContext: DSLContext
) {
    fun addRegistration(rr:RegistrationRecord) {
        dslContext.executeInsert(rr)
    }

    fun registrationListForWorkshop(workshopId: String): List<RegistrationRecord> {
        return dslContext.selectFrom(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.WORKSHOP.eq(workshopId))
            .forUpdate()
            .fetch()
    }

    fun cancelRegistration(registrationId: String) {
        dslContext.update(Tables.REGISTRATION)
            .set(Tables.REGISTRATION.CANCELLED_AT, OffsetDateTime.now())
            .set(Tables.REGISTRATION.STATUS,RegistrationStatus.CANCELLED.name)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .execute()
    }

    fun updateRegistrationStatus(registrationId: String,registrationStatus: RegistrationStatus) {
        dslContext.update(Tables.REGISTRATION)
            .set(Tables.REGISTRATION.STATUS,registrationStatus.name)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .execute()
    }

    fun registrationForId(registrationId: String): RegistrationRecord? {
        return dslContext
            .selectFrom(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .fetchOne()
    }

    fun registationListByParticipant(participantId:String):List<RegistrationRecord> {
        return dslContext
            .selectFrom(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.PARTICIPANT.eq(participantId))
            .fetch()
    }
}