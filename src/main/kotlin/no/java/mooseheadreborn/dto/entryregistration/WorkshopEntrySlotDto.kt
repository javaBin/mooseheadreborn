package no.java.mooseheadreborn.dto.entryregistration

data class WorkshopEntrySlotDto(
    val entryName:String,
    val workshopList:List<WorkshopEntryInfoDto>
) {
}