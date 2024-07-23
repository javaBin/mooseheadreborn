package no.java.mooseheadreborn.domain

enum class RegistrationStatus(val displayText: String) {
    NOT_LOGGED_IN("Not logged in"),
    NOT_REGISTERED("Not registered"),
    REGISTERED("Registered"),
    WAITING("On waiting list"),
    CANCELLED("Registration cancelled"),
}