package com.example.foodordering.domain

import java.io.Serializable

data class AddressModel(
    var street: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var phoneNumber: String = ""
) : Serializable {
    val formattedAddress: String
        get() = listOf(street, city, state, zipCode)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
