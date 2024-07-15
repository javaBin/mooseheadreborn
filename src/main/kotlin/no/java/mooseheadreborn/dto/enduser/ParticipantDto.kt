package no.java.mooseheadreborn.dto.enduser

data class ParticipantDto(
    val name:String,
    val email:String,
        val participationList:List<UserParticipationDto>
)