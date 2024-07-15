package no.java.mooseheadreborn.domain

enum class RegistrationStatus(val displayText: String) {
    REGISTERED("Registered"),
    WAITING("On waiting list"),
    CANCELLED("Registration cancelled"),
}