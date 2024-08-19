package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.jooq.public_.tables.records.*


class WorkshopRepositoryMock: WorkshopRepository {
    val store:MutableList<WorkshopRecord> = mutableListOf()
    override fun allWorkshops(): List<WorkshopRecord> = store

    override fun addWorkshop(workshopRecord: WorkshopRecord) {
        store.add(workshopRecord)
    }

    override fun workshopFromId(workshopId: String): WorkshopRecord? = store.firstOrNull { it.id == workshopId }

    override fun updateCapacity(workshopId: String, capacity: Int) {
        val workshopRecord:WorkshopRecord = store.firstOrNull { it.id == workshopId }?:return
        workshopRecord.setCapacity(capacity)
    }
}