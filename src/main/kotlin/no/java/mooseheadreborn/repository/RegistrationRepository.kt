package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.records.RegistrationRecord
import org.jooq.*
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import org.jooq.impl.DSL.sum

interface RegistrationRepository {
    fun addRegistration(rr: RegistrationRecord)
    fun registrationListForWorkshop(workshopId: String): List<RegistrationRecord>
    fun cancelRegistration(registrationId: String)
    fun updateRegistrationStatus(registrationId: String, registrationStatus: RegistrationStatus)
    fun registrationForId(registrationId: String): RegistrationRecord?
    fun registationListByParticipant(participantId: String): List<RegistrationRecord>
    fun allRegistrations(): List<RegistrationRecord>
    fun totalNumberOfRegistration():Map<String,Int>
    fun totalRegistrationsOnWorkshop(workshopId: String):Int
    fun setCheckedInAt(registrationId: String, checkedInAt: OffsetDateTime?)

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

    override fun totalNumberOfRegistration(): Map<String,Int> {
        val result = dslContext
            .select(sum(Tables.REGISTRATION.PARTICIPANT_COUNT).`as`("registerCount"),Tables.REGISTRATION.WORKSHOP)
            .from(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.STATUS.ne(RegistrationStatus.CANCELLED.name))
            .groupBy(Tables.REGISTRATION.WORKSHOP)
            .fetch()
        val resultMap:MutableMap<String,Int> = mutableMapOf()

        for (resultRow in result) {
            resultMap[resultRow.get(Tables.REGISTRATION.WORKSHOP)] = resultRow.get("registerCount",Int::class.java)
        }

        return resultMap
    }

    override fun totalRegistrationsOnWorkshop(workshopId: String): Int {
        val resultRow = dslContext
            .select(sum(Tables.REGISTRATION.PARTICIPANT_COUNT).`as`("registerCount"))
            .from(Tables.REGISTRATION)
            .where(Tables.REGISTRATION.WORKSHOP.eq(workshopId),Tables.REGISTRATION.STATUS.ne(RegistrationStatus.CANCELLED.name))
            .fetchOne()
        return resultRow?.get("registerCount",Int::class.java)?:0

    }

    override fun setCheckedInAt(registrationId: String, checkedInAt: OffsetDateTime?) {
        dslContext.update(Tables.REGISTRATION)
            .set(Tables.REGISTRATION.CHECKED_IN_AT,checkedInAt)
            .where(Tables.REGISTRATION.ID.eq(registrationId))
            .execute()
    }
}