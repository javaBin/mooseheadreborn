package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.jooq.public_.Tables
import org.jooq.*
import org.springframework.stereotype.Repository
import no.java.mooseheadreborn.jooq.public_.tables.records.AdminKeysRecord

@Repository
class AdminRepository(private val dslContext: DSLContext) {
    fun addKey(adminKeyRecord:AdminKeysRecord) {
        dslContext.executeInsert(adminKeyRecord)
    }

    fun readKey(key:String):AdminKeysRecord? {
        return dslContext
            .selectFrom(Tables.ADMIN_KEYS)
            .where(Tables.ADMIN_KEYS.KEY.eq(key))
            .fetchOne()
    }
}