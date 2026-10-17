package com.example.foodordering.domain

import java.io.Serializable

data class OrderModel(
    var orderId: String = "",
    var userId: String = "",
    var items: List<FoodModel> = emptyList(),
    var subtotal: Double = 0.0,
    var deliveryFee: Double = 0.0,
    var tax: Double = 0.0,
    var total: Double = 0.0,
    var address: AddressModel = AddressModel(),
    var paymentMethod: String = "",
    var status: String = "Placed",
    var timestamp: Long = System.currentTimeMillis()
) : Serializable
