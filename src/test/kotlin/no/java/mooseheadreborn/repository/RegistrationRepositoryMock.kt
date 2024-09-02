package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import java.time.*

class RegistrationRepositoryMock:RegistrationRepository {
    val store:MutableList<RegistrationRecord> = mutableListOf()

    override fun addRegistration(rr: RegistrationRecord) {
        store.add(rr)
    }

    override fun registrationListForWorkshop(workshopId: String): List<RegistrationRecord> = store.filter { it.workshop == workshopId }

    override fun cancelRegistration(registrationId: String) {
        val rr:RegistrationRecord = store.firstOrNull { it.id == registrationId }?:return
        rr.cancelledAt = OffsetDateTime.now()
        rr.status = RegistrationStatus.CANCELLED.name
    }

    override fun updateRegistrationStatus(registrationId: String, registrationStatus: RegistrationStatus) {
        val rr:RegistrationRecord = store.firstOrNull { it.id == registrationId }?:return
        rr.status = registrationStatus.name
    }

    override fun registrationForId(registrationId: String): RegistrationRecord? = store.firstOrNull { it.id == registrationId }

    override fun registationListByParticipant(participantId: String): List<RegistrationRecord> = store.filter{
        it.participant == participantId
    }

    override fun allRegistrations(): List<RegistrationRecord> = store

    override fun totalNumberOfRegistration(): Map<String, Int> {
        return store
            .filter { it.status != RegistrationStatus.CANCELLED.name }
            .groupBy { it.workshop }
            .mapValues { it.value.size }
    }

    override fun totalRegistrationsOnWorkshop(workshopId: String): Int {
        return store.count { it.workshop == workshopId  && it.status != RegistrationStatus.CANCELLED.name}
    }

    override fun setCheckedInAt(registrationId: String, checkedInAt: OffsetDateTime?) {
        TODO("Not yet implemented")
    }
}