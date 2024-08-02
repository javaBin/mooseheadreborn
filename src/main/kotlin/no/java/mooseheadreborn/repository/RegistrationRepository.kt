package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.records.RegistrationRecord
import org.jooq.*
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

interface RegistrationRepository {
    fun addRegistration(rr: RegistrationRecord)
    fun registrationListForWorkshop(workshopId: String): List<RegistrationRecord>
    fun cancelRegistration(registrationId: String)
    fun updateRegistrationStatus(registrationId: String, registrationStatus: RegistrationStatus)
    fun registrationForId(registrationId: String): RegistrationRecord?
    fun registationListByParticipant(participantId: String): List<RegistrationRecord>
    fun allRegistrations(): List<RegistrationRecord>

}

@Repository
class RegistrationRepositoryImpl(
    private val dslContext: DSLContext
):RegistrationRepository {
    override fun addRegistration(rr:RegistrationRecord) {
        dslContext.executeInsert(rr)
    }

    override fun registrationListForWorkshop(workshopId: String): List<RegistrationRecord> {
        return dslContext.selectFrom(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.WORKSHOP.eq(workshopId))
            .forUpdate()
            .fetch()
    }

    override fun cancelRegistration(registrationId: String) {
        dslContext.update(Tables.REGISTRATION)
            .set(Tables.REGISTRATION.CANCELLED_AT, OffsetDateTime.now())
            .set(Tables.REGISTRATION.STATUS,RegistrationStatus.CANCELLED.name)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .execute()
    }

    override fun updateRegistrationStatus(registrationId: String, registrationStatus: RegistrationStatus) {
        dslContext.update(Tables.REGISTRATION)
            .set(Tables.REGISTRATION.STATUS,registrationStatus.name)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .execute()
    }

    override fun registrationForId(registrationId: String): RegistrationRecord? {
        return dslContext
            .selectFrom(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .fetchOne()
    }

    override fun registationListByParticipant(participantId:String):List<RegistrationRecord> {
        return dslContext
            .selectFrom(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.PARTICIPANT.eq(participantId))
            .fetch()
    }

    override fun allRegistrations():List<RegistrationRecord> {
        return dslContext.selectFrom(Tables.REGISTRATION).fetch()
    }
}