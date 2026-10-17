package com.example.foodordering.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodordering.Repository.AuthRepository
import com.example.foodordering.Repository.OrderRepository
import com.example.foodordering.domain.OrderModel
import com.example.foodordering.domain.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ordersState = MutableStateFlow<UiState<List<OrderModel>>>(UiState.Loading)
    val ordersState: StateFlow<UiState<List<OrderModel>>> = _ordersState.asStateFlow()

    private val _selectedOrder = MutableStateFlow<OrderModel?>(null)
    val selectedOrder: StateFlow<OrderModel?> = _selectedOrder.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        val userId = authRepository.currentUserId
        if (userId.isNullOrEmpty()) {
            _ordersState.value = UiState.Empty
            return
        }

        viewModelScope.launch {
            _ordersState.value = UiState.Loading
            orderRepository.getOrders(userId)
                .catch { e ->
                    _ordersState.value = UiState.Error(e.message ?: "Failed to load orders")
                }
                .collect { orders ->
                    _ordersState.value = if (orders.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(orders)
                    }
                }
        }
    }

    fun selectOrder(order: OrderModel?) {
        _selectedOrder.value = order
    }

    fun selectOrderById(orderId: String) {
        val currentList = (_ordersState.value as? UiState.Success)?.data
        val found = currentList?.find { it.orderId == orderId }
        if (found != null) {
            _selectedOrder.value = found
        } else {
            val userId = authRepository.currentUserId ?: return
            viewModelScope.launch {
                orderRepository.getOrder(userId, orderId)
                    .collect { order ->
                        _selectedOrder.value = order
                    }
            }
        }
    }
}
