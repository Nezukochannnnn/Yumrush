package com.example.foodordering.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodordering.Repository.AuthRepository
import com.example.foodordering.Repository.OrderRepository
import com.example.foodordering.domain.OrderModel
import com.example.foodordering.domain.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DriverInfo(
    val name: String = "Alex Martinez",
    val vehicle: String = "Honda Civic • White • NY-3921",
    val phone: String = "+1 555-0182",
    val rating: String = "4.9 ⭐ (320+ deliveries)"
)

class OrderTrackingViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _order = MutableStateFlow<OrderModel?>(null)
    val order: StateFlow<OrderModel?> = _order.asStateFlow()

    private val _currentStatus = MutableStateFlow(OrderStatus.PLACED)
    val currentStatus: StateFlow<OrderStatus> = _currentStatus.asStateFlow()

    val driverInfo = DriverInfo()

    fun observeOrder(orderId: String) {
        val userId = authRepository.currentUserId ?: "guest"
        viewModelScope.launch {
            orderRepository.getOrder(userId, orderId).collect { retrievedOrder ->
                _order.value = retrievedOrder
                if (retrievedOrder != null) {
                    _currentStatus.value = OrderStatus.fromString(retrievedOrder.status)
                }
            }
        }
    }

    fun advanceStatus() {
        val current = _currentStatus.value
        val next = when (current) {
            OrderStatus.PLACED -> OrderStatus.CONFIRMED
            OrderStatus.CONFIRMED -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
            OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> OrderStatus.PLACED
        }

        val currentOrder = _order.value ?: return
        val userId = authRepository.currentUserId ?: "guest"

        val statusText = when (next) {
            OrderStatus.PLACED -> "Placed"
            OrderStatus.CONFIRMED -> "Confirmed"
            OrderStatus.PREPARING -> "Preparing"
            OrderStatus.OUT_FOR_DELIVERY -> "Out for Delivery"
            OrderStatus.DELIVERED -> "Delivered"
        }

        viewModelScope.launch {
            orderRepository.updateOrderStatus(userId, currentOrder.orderId, statusText)
        }
    }
}
