package no.java.mooseheadreborn.dto.enduser

data class UserParticipationDto(
    val workshopName:String,
    val participationStatus:String,
    val participantCount:Int?,
)