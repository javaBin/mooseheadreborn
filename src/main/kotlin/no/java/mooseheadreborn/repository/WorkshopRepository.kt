package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.records.WorkshopRecord
import org.jooq.*
import org.springframework.stereotype.*

interface WorkshopRepository {
    fun allWorkshops(): List<WorkshopRecord>
    fun addWorkshop(workshopRecord: WorkshopRecord)
    fun workshopFromId(workshopId: String): WorkshopRecord?

}

@Repository
class WorkshopRepositoryImpl(
    private val dslContext: DSLContext
):WorkshopRepository {
    override fun allWorkshops(): List<WorkshopRecord> {
        return dslContext.selectFrom(Tables.WORKSHOP).fetch()
    }

    override fun addWorkshop(workshopRecord: WorkshopRecord) {
        dslContext.executeInsert(workshopRecord )
    }

    override fun workshopFromId(workshopId: String): WorkshopRecord? {
        return dslContext.selectFrom(Tables.WORKSHOP)
            .where(Tables.WORKSHOP.ID.eq(workshopId))
            .fetchOne()
    }



}