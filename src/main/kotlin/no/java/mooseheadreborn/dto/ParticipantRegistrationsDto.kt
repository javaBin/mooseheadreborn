package no.java.mooseheadreborn.dto

import no.java.mooseheadreborn.domain.*

data class RegistrationInfoDto(
    val workshopName:String,
    val registrationStatus:RegistrationStatus,
    val registrationStatusText:String,
    val startTime:String?,
    val endTime:String?
)

data class ParticipantRegistrationsDto(
    val participantName:String,
    val registrationInfoList:List<RegistrationInfoDto>
)