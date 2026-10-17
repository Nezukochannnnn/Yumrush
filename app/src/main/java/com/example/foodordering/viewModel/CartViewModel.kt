package com.example.foodordering.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.foodordering.Helper.ManagmentCart
import com.example.foodordering.domain.FoodModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val managmentCart = ManagmentCart(application.applicationContext)

    private val _cartItems = MutableStateFlow<List<FoodModel>>(emptyList())
    val cartItems: StateFlow<List<FoodModel>> = _cartItems.asStateFlow()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    private val _deliveryFee = MutableStateFlow(0.0)
    val deliveryFee: StateFlow<Double> = _deliveryFee.asStateFlow()

    private val _tax = MutableStateFlow(0.0)
    val tax: StateFlow<Double> = _tax.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        val items = managmentCart.getListCart()
        _cartItems.value = items

        val sub = items.sumOf { it.Price * it.numberInCart }
        _subtotal.value = sub

        if (items.isEmpty()) {
            _deliveryFee.value = 0.0
            _tax.value = 0.0
            _total.value = 0.0
        } else {
            val delivery = 3.99
            val t = (sub * 0.08 * 100).toLong() / 100.0
            _deliveryFee.value = delivery
            _tax.value = t
            _total.value = sub + delivery + t
        }
    }

    fun addToCart(item: FoodModel) {
        managmentCart.insertItem(item)
        loadCart()
    }

    fun plusItem(position: Int) {
        val currentList = ArrayList(_cartItems.value)
        managmentCart.plusItem(currentList, position) {
            loadCart()
        }
    }

    fun minusItem(position: Int) {
        val currentList = ArrayList(_cartItems.value)
        managmentCart.minusItem(currentList, position) {
            loadCart()
        }
    }

    fun removeItem(position: Int) {
        managmentCart.removeItem(position) {
            loadCart()
        }
    }

    fun clearCart() {
        managmentCart.clearCart()
        loadCart()
    }
}
