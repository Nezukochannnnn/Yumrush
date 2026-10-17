package com.example.foodordering.domain

enum class PaymentMethod(val title: String, val subtitle: String) {
    CREDIT_CARD("Credit / Debit Card", "Visa, Mastercard, Amex"),
    GOOGLE_PAY("Google Pay / UPI", "Instant UPI or Wallet"),
    CASH_ON_DELIVERY("Cash on Delivery", "Pay in cash when order arrives")
}
