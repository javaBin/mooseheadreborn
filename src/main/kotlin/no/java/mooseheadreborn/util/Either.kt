package no.java.mooseheadreborn.util

sealed class Either<out L, out R> {
    data class Left<out L, out R>(val value: L) : Either<L, R>()
    data class Right<out L, out R>(val value: R) : Either<L, R>()

    fun <T> fold(left: (L) -> T, right: (R) -> T): T =
        when (this) {
            is Left -> left(value)
            is Right -> right(value)
        }

    fun leftOrNull():L? = when (this) {
        is Left -> value
        is Right -> null
    }

    fun leftOrError():L = when (this) {
        is Left -> value
        is Right -> throw RuntimeException("Value is not left")
    }

    fun rightOrNull():R? = when (this) {
        is Left -> null
        is Right -> value
    }
}