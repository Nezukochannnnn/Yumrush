package com.example.foodordering.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodordering.Repository.AuthRepository
import com.example.foodordering.Repository.OrderRepository
import com.example.foodordering.Repository.UserRepository
import com.example.foodordering.domain.SavedAddressModel
import com.example.foodordering.domain.UiState
import com.example.foodordering.domain.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    val currentUser: UserModel?
        get() = authRepository.currentUser

    private val _addressesState = MutableStateFlow<UiState<List<SavedAddressModel>>>(UiState.Loading)
    val addressesState: StateFlow<UiState<List<SavedAddressModel>>> = _addressesState.asStateFlow()

    private val _orderCount = MutableStateFlow(0)
    val orderCount: StateFlow<Int> = _orderCount.asStateFlow()

    init {
        loadAddresses()
        loadOrderStats()
    }

    fun loadAddresses() {
        val userId = authRepository.currentUserId
        if (userId.isNullOrEmpty()) {
            _addressesState.value = UiState.Empty
            return
        }

        viewModelScope.launch {
            _addressesState.value = UiState.Loading
            userRepository.getSavedAddresses(userId)
                .catch { e ->
                    _addressesState.value = UiState.Error(e.message ?: "Failed to load addresses")
                }
                .collect { list ->
                    _addressesState.value = if (list.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(list)
                    }
                }
        }
    }

    private fun loadOrderStats() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            orderRepository.getOrders(userId)
                .catch { }
                .collect { orders ->
                    _orderCount.value = orders.size
                }
        }
    }

    fun saveAddress(
        id: String = "",
        title: String,
        street: String,
        city: String,
        state: String,
        zip: String,
        phone: String,
        isDefault: Boolean
    ) {
        val userId = authRepository.currentUserId ?: return
        val model = SavedAddressModel(
            id = id,
            title = title,
            street = street,
            city = city,
            state = state,
            zipCode = zip,
            phoneNumber = phone,
            isDefault = isDefault
        )

        viewModelScope.launch {
            userRepository.saveAddress(userId, model)
        }
    }

    fun deleteAddress(addressId: String) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            userRepository.deleteAddress(userId, addressId)
        }
    }

    fun setDefaultAddress(addressId: String) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            userRepository.setDefaultAddress(userId, addressId)
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
