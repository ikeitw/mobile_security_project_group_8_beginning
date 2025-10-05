package com.group8.mobilesecurity.data.model

data class Geo(val lat: String, val lng: String)
data class Address(val street: String, val suite: String, val city: String, val zipcode: String, val geo: Geo)
data class Company(val name: String, val catchPhrase: String, val bs: String)

data class UserDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val address: Address,
    val company: Company
)
