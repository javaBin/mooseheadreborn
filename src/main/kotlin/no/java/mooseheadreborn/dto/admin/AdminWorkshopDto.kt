package no.java.mooseheadreborn.dto.admin

import no.java.mooseheadreborn.domain.*

class AdminWorkshopDto(
    val id:String,
    val name:String,
    val workshopType:WorkshopType,
    val workshopstatus: WorkshopStatus,
    val opensAt:String,
    val registerLimit:Int,
    val capacity:Int,
    val seatsTaken:Int,
    val registrationList:List<AdminWorkshopRegistration>
) {
}