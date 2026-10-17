package com.example.foodordering.domain

import java.io.Serializable

data class SavedAddressModel(
    var id: String = "",
    var title: String = "Home",
    var street: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var phoneNumber: String = "",
    var isDefault: Boolean = false
) : Serializable {
    fun toAddressModel(): AddressModel {
        return AddressModel(
            street = street,
            city = city,
            state = state,
            zipCode = zipCode,
            phoneNumber = phoneNumber
        )
    }

    val formattedAddress: String
        get() = listOf(street, city, state, zipCode)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
