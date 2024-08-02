package no.java.mooseheadreborn.domain

enum class WorkshopStatus(val text:String) {
    OPEN("Open for registration"),
    NOT_OPEN("Registration has not opened yet"),
    FULL("Workshop is full. You can register to the waiting list"),
    CLOSED("Registrations are closed"),
    ;


}