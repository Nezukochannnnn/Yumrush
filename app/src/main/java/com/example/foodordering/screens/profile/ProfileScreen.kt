package com.example.foodordering.screens.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.R
import com.example.foodordering.domain.UiState
import com.example.foodordering.screens.dashboard.MyBottomBar
import com.example.foodordering.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser = profileViewModel.currentUser
    val orderCount by profileViewModel.orderCount.collectAsState()
    val addressesState by profileViewModel.addressesState.collectAsState()

    val addressCount = when (val state = addressesState) {
        is UiState.Success -> state.data.size
        else -> 0
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colorResource(R.color.black2),
        bottomBar = {
            MyBottomBar(
                selectedTab = "Profile",
                onTabSelected = { tab ->
                    when (tab) {
                        "Home" -> onNavigateToHome()
                        "Cart" -> onNavigateToCart()
                        "Order" -> onNavigateToOrders()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Screen Header
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "My Profile",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // User Info Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorResource(R.color.black3))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar Image with glowing border
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, colorResource(R.color.orange), CircleShape)
                            .background(colorResource(R.color.black2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val displayName = when {
                            !currentUser?.displayName.isNullOrBlank() -> currentUser.displayName
                            !currentUser?.email.isNullOrBlank() -> currentUser.email.substringBefore('@')
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            else -> "Foodie Explorer"
                        }

                        Text(
                            text = displayName,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = currentUser?.email.orEmpty().ifBlank { "Registered Member" },
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Member badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colorResource(R.color.orange).copy(alpha = 0.2f))
                                .border(
                                    width = 1.dp,
                                    color = colorResource(R.color.orange).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⭐ Foodies Gold Member",
                                color = colorResource(R.color.orange),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    title = "Orders",
                    value = "$orderCount",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "Addresses",
                    value = "$addressCount",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "Points",
                    value = "${orderCount * 25}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Activities & Orders
            Text(
                text = "My Activity",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorResource(R.color.black3))
            ) {
                ProfileMenuItem(
                    iconPainter = painterResource(R.drawable.btn_4),
                    title = "My Orders",
                    subtitle = "History and live tracking",
                    onClick = onNavigateToOrders
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    iconEmoji = "📍",
                    title = "Saved Addresses",
                    subtitle = "$addressCount saved delivery locations",
                    onClick = onNavigateToSavedAddresses
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    iconPainter = painterResource(R.drawable.btn_2),
                    title = "My Cart",
                    subtitle = "Review items ready for checkout",
                    onClick = onNavigateToCart
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Support & About
            Text(
                text = "Support & Information",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorResource(R.color.black3))
            ) {
                ProfileMenuItem(
                    iconEmoji = "🎧",
                    title = "Customer Support",
                    subtitle = "24/7 delivery assistance",
                    onClick = { showSupportDialog = true }
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    iconEmoji = "ℹ️",
                    title = "About Foodies",
                    subtitle = "Version 1.0.0 (Jetpack Compose)",
                    onClick = { showAboutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFD32F2F).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFFD32F2F).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { showLogoutDialog = true }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🚪",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        color = Color(0xFFFF6B6B),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Sign Out Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = colorResource(R.color.black3),
            title = {
                Text(
                    text = "Sign Out",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of your account?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        profileViewModel.signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }

    // Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            containerColor = colorResource(R.color.black3),
            title = {
                Text(
                    text = "Customer Support",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Need help with an order or account issue?",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "📞 Hotline: 1-800-FOODIES\n📧 Email: support@foodiesapp.com\n⏰ Hours: 24 hours / 7 days",
                        color = colorResource(R.color.orange),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSupportDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange)
                    )
                ) {
                    Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = colorResource(R.color.black3),
            title = {
                Text(
                    text = "Foodies Delivery",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Version 1.0.0 (Production Build)",
                        color = colorResource(R.color.orange),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Built with modern Jetpack Compose, Clean Architecture, Kotlin Coroutines & Flow, and Firebase Realtime Database.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange)
                    )
                ) {
                    Text("Got It", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ProfileStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(R.color.black3))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = colorResource(R.color.orange),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconPainter: Painter? = null,
    iconEmoji: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.orange).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (iconPainter != null) {
                Image(
                    painter = iconPainter,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp)
                )
            } else if (iconEmoji != null) {
                Text(
                    text = iconEmoji,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        Text(
            text = "›",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 22.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun ProfileMenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.08f))
    )
}
