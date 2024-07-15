package no.java.mooseheadreborn.dto

data class AddRegistrationDto(
    val accessToken:String,
    val workshopId:String,
    val numParticipants:Int?
)