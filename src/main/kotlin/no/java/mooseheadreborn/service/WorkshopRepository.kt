package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.records.WorkshopRecord
import org.jooq.*
import org.springframework.stereotype.*

@Repository
class WorkshopRepository(
    private val dslContext: DSLContext
) {
    fun allWorkshops(): List<WorkshopRecord> {
        return dslContext.selectFrom(Tables.WORKSHOP).fetch()
    }

    fun addWorkshop(workshopRecord: WorkshopRecord) {
        dslContext.executeInsert(workshopRecord )
    }



}