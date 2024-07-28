package no.java.mooseheadreborn.dto

import no.java.mooseheadreborn.domain.*

class UserWorkshopRegistrationDto(
    val workshop: WorkshopDto,
    val registrationStatus:RegistrationStatus,
    val registrationStatusText:String,
    val registrationId:String?,
    val numRegistered:Int?,
) {
}