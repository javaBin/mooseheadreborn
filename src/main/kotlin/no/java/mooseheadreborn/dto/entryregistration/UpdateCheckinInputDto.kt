package no.java.mooseheadreborn.dto.entryregistration

data class UpdateCheckinInputDto(
    val accessToken:String,
    val registrationId:String,
    val setCheckinTo:Boolean,
)