package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.jooq.public_.Tables
import no.java.mooseheadreborn.jooq.public_.tables.Particiant
import no.java.mooseheadreborn.jooq.public_.tables.records.ParticiantRecord
import org.jooq.*
import org.springframework.stereotype.Repository
import java.time.*

@Repository
class ParticipantRepository(
    private val dslContext: DSLContext
) {
    fun addParticipant(pr:ParticiantRecord) {
        dslContext.executeInsert(pr)
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

    fun setActive(id:String) {
        dslContext.update(Tables.PARTICIANT)
            .set(Particiant.PARTICIANT.ACTIVATED_AT, OffsetDateTime.now())
            .where(Particiant.PARTICIANT.ID.eq(id))
            .execute()
    }

}