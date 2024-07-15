package no.java.mooseheadreborn.dto

class NoDataDto {
    override fun equals(other: Any?): Boolean {
        return (other is NoDataDto)
    }

    override fun hashCode(): Int {
        return 1
    }
}