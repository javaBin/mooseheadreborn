package no.java.mooseheadreborn.dto.entryregistration

import no.java.mooseheadreborn.repository.*

data class EntryRegistrationForWorkshopDto(
    val entryList:List<WorkshopEntryRegistration>,
    val numberCheckedIn:Int,
)