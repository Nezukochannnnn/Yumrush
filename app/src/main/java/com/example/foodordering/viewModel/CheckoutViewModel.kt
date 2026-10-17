package com.example.foodordering.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodordering.Repository.AuthRepository
import com.example.foodordering.Repository.OrderRepository
import com.example.foodordering.Repository.UserRepository
import com.example.foodordering.domain.AddressModel
import com.example.foodordering.domain.FoodModel
import com.example.foodordering.domain.OrderModel
import com.example.foodordering.domain.PaymentMethod
import com.example.foodordering.domain.SavedAddressModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface CheckoutUiState {
    data object Idle : CheckoutUiState
    data object Processing : CheckoutUiState
    data class Success(val orderId: String) : CheckoutUiState
    data class Error(val message: String) : CheckoutUiState
}

class CheckoutViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _address = MutableStateFlow(
        AddressModel(
            street = "123 Main Street, Apt 4B",
            city = "New York",
            state = "NY",
            zipCode = "10001",
            phoneNumber = "+1 555-0199"
        )
    )
    val address: StateFlow<AddressModel> = _address.asStateFlow()

    private val _savedAddresses = MutableStateFlow<List<SavedAddressModel>>(emptyList())
    val savedAddresses: StateFlow<List<SavedAddressModel>> = _savedAddresses.asStateFlow()

    private val _paymentMethod = MutableStateFlow(PaymentMethod.CREDIT_CARD)
    val paymentMethod: StateFlow<PaymentMethod> = _paymentMethod.asStateFlow()

    private val _checkoutState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    init {
        loadSavedAddresses()
    }

    fun loadSavedAddresses() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            userRepository.getSavedAddresses(userId)
                .catch { }
                .collect { list ->
                    _savedAddresses.value = list
                    val defaultAddr = list.firstOrNull { it.isDefault } ?: list.firstOrNull()
                    if (defaultAddr != null) {
                        _address.value = defaultAddr.toAddressModel()
                    }
                }
        }
    }

    fun selectSavedAddress(savedAddress: SavedAddressModel) {
        _address.value = savedAddress.toAddressModel()
    }

    fun updateStreet(street: String) {
        _address.value = _address.value.copy(street = street)
    }

    fun updateCity(city: String) {
        _address.value = _address.value.copy(city = city)
    }

    fun updateState(state: String) {
        _address.value = _address.value.copy(state = state)
    }

    fun updateZip(zip: String) {
        _address.value = _address.value.copy(zipCode = zip)
    }

    fun updatePhone(phone: String) {
        _address.value = _address.value.copy(phoneNumber = phone)
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _paymentMethod.value = method
    }

    fun resetState() {
        _checkoutState.value = CheckoutUiState.Idle
    }

    fun processOrder(
        items: List<FoodModel>,
        subtotal: Double,
        deliveryFee: Double,
        tax: Double,
        total: Double,
        onCartCleared: () -> Unit
    ) {
        val currentAddr = _address.value
        if (currentAddr.street.isBlank()) {
            _checkoutState.value = CheckoutUiState.Error("Please enter your street address")
            return
        }
        if (currentAddr.city.isBlank()) {
            _checkoutState.value = CheckoutUiState.Error("Please enter your city")
            return
        }
        if (currentAddr.phoneNumber.isBlank()) {
            _checkoutState.value = CheckoutUiState.Error("Please enter a contact phone number")
            return
        }
        if (items.isEmpty()) {
            _checkoutState.value = CheckoutUiState.Error("Your cart is empty")
            return
        }

        viewModelScope.launch {
            _checkoutState.value = CheckoutUiState.Processing

            // Mock payment processing delay (2 seconds)
            delay(2000)

            val orderId = "ORD-" + (100000..999999).random()
            val userId = authRepository.currentUserId ?: "guest"

            val order = OrderModel(
                orderId = orderId,
                userId = userId,
                items = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                tax = tax,
                total = total,
                address = currentAddr,
                paymentMethod = _paymentMethod.value.title,
                status = "Placed",
                timestamp = System.currentTimeMillis()
            )

            orderRepository.placeOrder(order).fold(
                onSuccess = {
                    onCartCleared()
                    _checkoutState.value = CheckoutUiState.Success(orderId)
                },
                onFailure = { error ->
                    _checkoutState.value = CheckoutUiState.Error(
                        error.localizedMessage ?: "Failed to process order. Please try again."
                    )
                }
            )
        }
    }
}
