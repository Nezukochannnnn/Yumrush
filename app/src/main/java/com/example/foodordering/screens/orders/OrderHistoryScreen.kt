package com.example.foodordering.screens.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodordering.R
import com.example.foodordering.domain.OrderModel
import com.example.foodordering.domain.UiState
import com.example.foodordering.screens.common.EmptySection
import com.example.foodordering.screens.common.ErrorSection
import com.example.foodordering.screens.common.OrderCardShimmer
import com.example.foodordering.screens.dashboard.MyBottomBar
import com.example.foodordering.viewModel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onOrderClick: (OrderModel) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateCart: () -> Unit,
    onNavigateProfile: () -> Unit = {}
) {
    val ordersState by orderViewModel.ordersState.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadOrders()
    }

    Scaffold(
        containerColor = colorResource(R.color.black2),
        bottomBar = {
            MyBottomBar(
                selectedTab = "Order",
                onTabSelected = { tab ->
                    when (tab) {
                        "Home" -> onNavigateHome()
                        "Cart" -> onNavigateCart()
                        "Profile" -> onNavigateProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Bar
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                val (backBtn, titleTxt, refreshBtn) = createRefs()

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
                    text = "My Orders",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(titleTxt) {
                        centerTo(parent)
                    }
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.black3))
                        .clickable { orderViewModel.loadOrders() }
                        .constrainAs(refreshBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↻",
                        color = colorResource(R.color.orange),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (val state = ordersState) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        repeat(3) {
                            OrderCardShimmer()
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorSection(
                        message = state.message,
                        onRetry = { orderViewModel.loadOrders() }
                    )
                }
                is UiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📋",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "No Orders Yet",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "When you order your favorite meals, they will appear here.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = onNavigateHome,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.orange)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Order Food Now",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { order ->
                            OrderCard(
                                order = order,
                                onClick = { onOrderClick(order) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderModel,
    onClick: () -> Unit
) {
    val dateString = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        .format(Date(order.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.black3),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Order ID and Status Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${order.orderId}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            OrderStatusBadge(status = order.status)
        }

        Text(
            text = dateString,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Items preview
        val itemsPreview = order.items.joinToString(", ") { "${it.numberInCart}x ${it.Title}" }
        Text(
            text = itemsPreview.ifEmpty { "Items ordered" },
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Bottom row: Total and Details link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Total: ",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", order.total)}",
                    color = colorResource(R.color.orange),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "View Details →",
                color = colorResource(R.color.orange),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (badgeBg, badgeTextColor) = when (status.lowercase()) {
        "delivered" -> Color(0xFF0FB85B).copy(alpha = 0.2f) to Color(0xFF0FB85B)
        "out for delivery" -> Color(0xFF9C27B0).copy(alpha = 0.2f) to Color(0xFFCE93D8)
        "preparing" -> Color(0xFFFF9800).copy(alpha = 0.2f) to Color(0xFFFFB74D)
        else -> Color(0xFF2196F3).copy(alpha = 0.2f) to Color(0xFF64B5F6)
    }

    Box(
        modifier = Modifier
            .background(color = badgeBg, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = badgeTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
