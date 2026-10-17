package com.example.foodordering.domain

enum class FoodFilter(val title: String) {
    ALL("All"),
    TOP_RATED("Top Rated ⭐ 4.5+"),
    FAST_DELIVERY("Fast Delivery ⏱️"),
    UNDER_10("Budget Bites < $10")
}
