package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.domain.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.*
import java.sql.Timestamp
import java.time.*
import java.time.format.*

class RegistrationCollision(
    val name:String,
    val email:String,
    val workshopAName:String,
    val workshopBName:String,
    val statusA:RegistrationStatus,
    val statusB:RegistrationStatus,
    val astart:String,
    val aend:String,
    val bstart:String,
    val bend:String,
    val registrationIdA:String,
    val registrationIdB:String,
    val participantId:String,
)

data class WorkshopEntryRegistration(
    val registrationNumber:Int,
    val registrationId:String,
    val participantName:String,
    val participantEmail:String,
    val partcipantId:String,
    val registrationStatus: RegistrationStatus,
    val isCheckedIn:Boolean,

)

@Repository
class ReportRepository(
    private val jdbcTemplate:NamedParameterJdbcTemplate
) {
    companion object {
        private val zoneId = ZoneId.of("Europe/Oslo")
        private val dateFormat = DateTimeFormatter.ofPattern("dd/MM HH:mm")

        private fun timestampAsLocalText(timestamp: Timestamp):String {
            return timestamp.toInstant().atZone(zoneId).toLocalDateTime().format(dateFormat)
        }
    }


    fun loadRegistrationCollisionList():List<RegistrationCollision> {
        val res:List<RegistrationCollision> = jdbcTemplate.query("""   
            SELECT par.name, par.email, wa.name as aname, wb.name as bname, ra.status as astatus, rb.status as bstatus, wa.starttime as astart,
            wa.endtime as aend, wb.starttime as bstart, wb.endtime as bend, ra.id as regaid, rb.id as regbid, par.id as participantid
            FROM workshop wa, workshop wb, registration ra, registration rb, particiant par
            WHERE wa.workshop_type = 'JZ' AND wb.workshop_type = 'JZ' AND wa.id <> wb.id
            AND wa.starttime IS NOT NULL AND wb.starttime IS NOT NULL AND wa.endtime IS NOT NULL AND wb.endtime IS NOT NULL 
            AND wa.starttime <= wb.starttime AND wa.endtime > wb.starttime AND
            (wa.starttime <> wb.starttime OR wb.starttime <> wa.starttime OR wa.id < wb.id)
            AND ra.workshop = wa.id AND rb.workshop = wb.id AND ra.participant = rb.participant
            AND ra.status IN ('REGISTERED','WAITING') AND rb.status IN ('REGISTERED','WAITING')
            AND ra.participant = par.id
            ORDER BY ra.status, rb.status                                 
        """.trimIndent()) {  rs, _ ->
                RegistrationCollision(
                    name = rs.getString("name"),
                    email = rs.getString("email"),
                    workshopAName = rs.getString("aname"),
                    workshopBName = rs.getString("bname"),
                    statusA = RegistrationStatus.valueOf(rs.getString("astatus")),
                    statusB = RegistrationStatus.valueOf(rs.getString("bstatus")),
                    astart = timestampAsLocalText(rs.getTimestamp("astart")),
                    aend = timestampAsLocalText(rs.getTimestamp("aend")),
                    bstart = timestampAsLocalText(rs.getTimestamp("bstart")),
                    bend = timestampAsLocalText(rs.getTimestamp("bend")),
                    registrationIdA = rs.getString("regaid"),
                    registrationIdB = rs.getString("regbid"),
                    participantId = rs.getString("participantid")
                )

        }
        return res
    }

    fun loadEntryRegistration(workshopid:String):List<WorkshopEntryRegistration> {
        return jdbcTemplate.query("""
            SELECT r.id as registrationid, p.name,p.email, p.id as participantid, r.status, r.checked_in_at
            FROM registration r, particiant p
            WHERE r.workshop = :workshopid AND r.status IN ('REGISTERED','WAITING') AND
              r.participant = p.id ORDER BY r.registered_at
        """.trimIndent(), mapOf("workshopid" to workshopid)) { rs, rowNum ->
            WorkshopEntryRegistration(
                registrationNumber = rowNum+1,
                registrationId = rs.getString("registrationid"),
                partcipantId = rs.getString("participantid"),
                participantName = rs.getString("name"),
                participantEmail = rs.getString("email"),
                registrationStatus = RegistrationStatus.valueOf(rs.getString("status")),
                isCheckedIn = rs.getTimestamp("checked_in_at") != null,
            )

        }
    }
}