package no.java.mooseheadreborn.dto

import no.java.mooseheadreborn.domain.*

data class WorkshopDto(
    val id:String,
    val name:String,
    val workshopstatus:WorkshopStatus,
)