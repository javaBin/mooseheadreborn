package no.java.mooseheadreborn.domain

data class UserDto (
    val accessToken:String?,
    val name:String?,
    val email:String?,
    val userType: UserType
)