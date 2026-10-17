package com.example.foodordering.domain

enum class OrderStatus(
    val stepIndex: Int,
    val title: String,
    val description: String
) {
    PLACED(0, "Order Placed", "Your order has been received"),
    CONFIRMED(1, "Order Confirmed", "Restaurant has accepted your order"),
    PREPARING(2, "Kitchen Preparing", "Chef is cooking your fresh dishes"),
    OUT_FOR_DELIVERY(3, "Out for Delivery", "Driver is on the way to your address"),
    DELIVERED(4, "Delivered", "Enjoy your delicious meal!");

    companion object {
        fun fromString(status: String): OrderStatus {
            return when (status.trim().lowercase()) {
                "confirmed" -> CONFIRMED
                "preparing" -> PREPARING
                "out for delivery" -> OUT_FOR_DELIVERY
                "delivered" -> DELIVERED
                else -> PLACED
            }
        }
    }
}
