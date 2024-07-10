package no.java.mooseheadreborn.domain

enum class WorkshopType(
    val registerLimit:Int
) {
    JZ(1),
    KIDS(3),
    ;

    companion object {
        fun fromString(value: String): WorkshopType? =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}