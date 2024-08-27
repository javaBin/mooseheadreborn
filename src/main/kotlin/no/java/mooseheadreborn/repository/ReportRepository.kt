package no.java.mooseheadreborn.repository

import no.java.mooseheadreborn.domain.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.*

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
)

@Repository
class ReportRepository(
    private val jdbcTemplate:JdbcTemplate
) {
    fun loadRegistrationCollisionList():List<RegistrationCollision> {
        val res:List<RegistrationCollision> = jdbcTemplate.query("""   
            SELECT par.name, par.email, wa.name as aname, wb.name as bname, ra.status as astatus, rb.status as bstatus, wa.starttime as astart,
            wa.endtime as aend, wb.starttime as bstart, wb.endtime as bend, ra.id as regaid, rb.id as regbid
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
                    astart = rs.getTimestamp("astart").toInstant().toString(),
                    aend = rs.getTimestamp("aend").toInstant().toString(),
                    bstart = rs.getTimestamp("bstart").toInstant().toString(),
                    bend = rs.getTimestamp("bend").toInstant().toString(),
                    registrationIdA = rs.getString("regaid"),
                    registrationIdB = rs.getString("regbid")
                )

        }
        return res
    }
}