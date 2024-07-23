package no.java.mooseheadreborn.dto

import no.java.mooseheadreborn.domain.*

data class AddRegistrationResultDto(
    val registrationStatus: RegistrationStatus,
    val registrationId:String,

)