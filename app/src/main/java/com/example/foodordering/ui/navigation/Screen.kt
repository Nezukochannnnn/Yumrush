package com.example.foodordering.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen(route = "login")
    data object Signup : Screen(route = "signup")
    data object Home : Screen(route = "home")
    data object Cart : Screen(route = "cart")
    data object Checkout : Screen(route = "checkout")
    data object Orders : Screen(route = "orders")

    data object OrderDetail : Screen(route = "orderDetail/{orderId}") {
        fun path(orderId: String) = "orderDetail/$orderId"
    }

    data object OrderTracking : Screen(route = "orderTracking/{orderId}") {
        fun path(orderId: String) = "orderTracking/$orderId"
    }

    data object OrderSuccess : Screen(route = "orderSuccess/{orderId}") {
        fun path(orderId: String) = "orderSuccess/$orderId"
    }

    data object Items : Screen(route = "itemsList/{id}/{title}") {
        fun path(id: String, title: String) =
            "itemsList/$id/${java.net.URLEncoder.encode(title, Charsets.UTF_8.name())}"
    }

    data object Detail : Screen(route = "detail")
    data object Profile : Screen(route = "profile")
    data object SavedAddresses : Screen(route = "savedAddresses")
}