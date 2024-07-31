package no.java.mooseheadreborn.dto.admin

import no.java.mooseheadreborn.domain.*

class AdminWorkshopRegistration(
    val id:String,
    val status:RegistrationStatus,
    val name:String,
    val email:String,
    val numSpots:Int,
    val participantId:String,
    val registeredAt:String,
)