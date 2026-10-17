package com.example.foodordering.screens.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodordering.R
import com.example.foodordering.domain.PaymentMethod
import com.example.foodordering.screens.cart.SummaryRow
import com.example.foodordering.viewModel.CartViewModel
import com.example.foodordering.viewModel.CheckoutUiState
import com.example.foodordering.viewModel.CheckoutViewModel
import java.util.Locale

@Composable
fun CheckoutScreen(
    checkoutViewModel: CheckoutViewModel,
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderPlaced: (orderId: String) -> Unit
) {
    val address by checkoutViewModel.address.collectAsState()
    val savedAddresses by checkoutViewModel.savedAddresses.collectAsState()
    val paymentMethod by checkoutViewModel.paymentMethod.collectAsState()
    val checkoutState by checkoutViewModel.checkoutState.collectAsState()

    val cartItems by cartViewModel.cartItems.collectAsState()
    val subtotal by cartViewModel.subtotal.collectAsState()
    val deliveryFee by cartViewModel.deliveryFee.collectAsState()
    val tax by cartViewModel.tax.collectAsState()
    val total by cartViewModel.total.collectAsState()

    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutUiState.Success) {
            val orderId = (checkoutState as CheckoutUiState.Success).orderId
            checkoutViewModel.resetState()
            onOrderPlaced(orderId)
        }
    }

    Scaffold(
        containerColor = colorResource(R.color.black2),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.black3))
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = {
                        checkoutViewModel.processOrder(
                            items = cartItems,
                            subtotal = subtotal,
                            deliveryFee = deliveryFee,
                            tax = tax,
                            total = total,
                            onCartCleared = { cartViewModel.clearCart() }
                        )
                    },
                    enabled = checkoutState !is CheckoutUiState.Processing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange),
                        disabledContainerColor = colorResource(R.color.orange).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (checkoutState is CheckoutUiState.Processing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Authorizing Payment...",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pay & Place Order",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", total)}",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                val (backBtn, titleTxt) = createRefs()

                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .size(32.dp)
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    text = "Checkout",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(titleTxt) {
                        centerTo(parent)
                    }
                )
            }

            // Error display
            if (checkoutState is CheckoutUiState.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(
                            color = Color(0xFFD32F2F).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = (checkoutState as CheckoutUiState.Error).message,
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp
                    )
                }
            }

            // 1. Delivery Address Section
            Text(
                text = "Delivery Address",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(R.color.black3),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (savedAddresses.isNotEmpty()) {
                    Text(
                        text = "Saved Addresses:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        savedAddresses.forEach { saved ->
                            val isSelected = (address.street == saved.street && address.city == saved.city)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) colorResource(R.color.orange)
                                        else colorResource(R.color.black2)
                                    )
                                    .clickable { checkoutViewModel.selectSavedAddress(saved) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${if (saved.title.isNotBlank()) saved.title else "Address"}${if (saved.isDefault) " ★" else ""}",
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                OutlinedTextField(
                    value = address.street,
                    onValueChange = { checkoutViewModel.updateStreet(it) },
                    label = { Text("Street Address") },
                    singleLine = true,
                    colors = checkoutTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = address.city,
                        onValueChange = { checkoutViewModel.updateCity(it) },
                        label = { Text("City") },
                        singleLine = true,
                        colors = checkoutTextFieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = address.zipCode,
                        onValueChange = { checkoutViewModel.updateZip(it) },
                        label = { Text("Zip Code") },
                        singleLine = true,
                        colors = checkoutTextFieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = address.phoneNumber,
                    onValueChange = { checkoutViewModel.updatePhone(it) },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    colors = checkoutTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Payment Method Section
            Text(
                text = "Payment Method",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PaymentMethod.entries.forEach { method ->
                    val isSelected = method == paymentMethod
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(R.color.black3),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) colorResource(R.color.orange) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { checkoutViewModel.setPaymentMethod(method) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Radio circle indicator
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) colorResource(R.color.orange) else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(colorResource(R.color.orange), CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = method.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = method.subtitle,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Order Summary Section
            Text(
                text = "Order Breakdown (${cartItems.size} items)",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(R.color.black3),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp)
            ) {
                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.numberInCart}x ${item.Title}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", item.Price * item.numberInCart)}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SummaryRow("Subtotal", "$${String.format(Locale.US, "%.2f", subtotal)}")
                SummaryRow("Delivery Fee", "$${String.format(Locale.US, "%.2f", deliveryFee)}")
                SummaryRow("Estimated Tax", "$${String.format(Locale.US, "%.2f", tax)}")

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total to Pay",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", total)}",
                        color = colorResource(R.color.orange),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun checkoutTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = colorResource(R.color.black2),
    unfocusedContainerColor = colorResource(R.color.black2),
    focusedBorderColor = colorResource(R.color.orange),
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = colorResource(R.color.orange),
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
    cursorColor = colorResource(R.color.orange)
)
