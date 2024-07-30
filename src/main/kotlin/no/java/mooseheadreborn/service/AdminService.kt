package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.tables.records.AdminKeysRecord
import no.java.mooseheadreborn.util.*
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class AdminService(private val adminRepository: AdminRepository) {
    fun createAccess(password:String):Either<UserDto,String> {
        if (Config.getConfigValue(ConfigVariable.ADMIN_PASSWORD) != password) {
            return Either.Right("No access");
        }
        val key = UUID.randomUUID().toString()
        val created = OffsetDateTime.now()
        val adminKeyRecord = AdminKeysRecord(key, created)
        adminRepository.addKey(adminKeyRecord)
        val userDto = UserDto(key, "ADMIN", "program@java.no", UserType.ADMIN)
        return Either.Left(userDto)
    }

    fun keyIsValid(key:String):Boolean {
        val adminRecord:AdminKeysRecord? = adminRepository.readKey(key)
        return (adminRecord != null)
    }
}