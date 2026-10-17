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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodordering.R
import com.example.foodordering.domain.SavedAddressModel
import com.example.foodordering.domain.UiState
import com.example.foodordering.screens.common.AddressCardShimmer
import com.example.foodordering.screens.common.ErrorSection
import com.example.foodordering.viewModel.ProfileViewModel

@Composable
fun SavedAddressesScreen(
    profileViewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val addressesState by profileViewModel.addressesState.collectAsState()

    var showAddressDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<SavedAddressModel?>(null) }
    var addressToDelete by remember { mutableStateOf<SavedAddressModel?>(null) }

    Scaffold(
        containerColor = colorResource(R.color.black2)
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
                val (backBtn, titleTxt, addBtn) = createRefs()

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
                    text = "Saved Addresses",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(titleTxt) {
                        centerTo(parent)
                    }
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorResource(R.color.orange))
                        .clickable {
                            editingAddress = null
                            showAddressDialog = true
                        }
                        .size(32.dp)
                        .constrainAs(addBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Body
            when (val state = addressesState) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) {
                            AddressCardShimmer()
                        }
                    }
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
                            text = "📍",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "No Saved Addresses",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Save your home, work, or frequent delivery spots for fast checkout.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = {
                                editingAddress = null
                                showAddressDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.orange)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Add New Address",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorSection(
                        message = state.message,
                        onRetry = { profileViewModel.loadAddresses() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data, key = { it.id }) { address ->
                            SavedAddressCard(
                                address = address,
                                onEdit = {
                                    editingAddress = address
                                    showAddressDialog = true
                                },
                                onDelete = {
                                    addressToDelete = address
                                },
                                onSetDefault = {
                                    profileViewModel.setDefaultAddress(address.id)
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Address Dialog
    if (showAddressDialog) {
        AddressEditorDialog(
            initialAddress = editingAddress,
            onDismiss = {
                showAddressDialog = false
                editingAddress = null
            },
            onSave = { title, street, city, state, zip, phone, isDefault ->
                profileViewModel.saveAddress(
                    id = editingAddress?.id.orEmpty(),
                    title = title,
                    street = street,
                    city = city,
                    state = state,
                    zip = zip,
                    phone = phone,
                    isDefault = isDefault
                )
                showAddressDialog = false
                editingAddress = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (addressToDelete != null) {
        val target = addressToDelete!!
        AlertDialog(
            onDismissRequest = { addressToDelete = null },
            containerColor = colorResource(R.color.black3),
            title = {
                Text(
                    text = "Delete Address",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${target.title}\" (${target.street})?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        profileViewModel.deleteAddress(target.id)
                        addressToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { addressToDelete = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}

@Composable
fun SavedAddressCard(
    address: SavedAddressModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    val isDefault = address.isDefault

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colorResource(R.color.black3))
            .border(
                width = if (isDefault) 1.5.dp else 0.dp,
                color = if (isDefault) colorResource(R.color.orange) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(16.dp)
    ) {
        // Header Row: Tag pill + Default badge + Action icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Chip
                val chipColor = when (address.title.lowercase()) {
                    "home" -> colorResource(R.color.orange)
                    "work" -> Color(0xFF4FC3F7)
                    else -> Color(0xFFBA68C8)
                }

                val iconTag = when (address.title.lowercase()) {
                    "home" -> "🏠 "
                    "work" -> "🏢 "
                    else -> "📍 "
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(chipColor.copy(alpha = 0.2f))
                        .border(1.dp, chipColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = iconTag + address.title.ifBlank { "Address" },
                        color = chipColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isDefault) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0FB85B).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF0FB85B).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DEFAULT",
                            color = Color(0xFF0FB85B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Edit and Delete buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onEdit() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Edit",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF5252).copy(alpha = 0.15f))
                        .clickable { onDelete() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Street address
        Text(
            text = address.street,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        // City, State Zip
        val cityStateZip = listOf(address.city, address.state, address.zipCode)
            .filter { it.isNotBlank() }
            .joinToString(", ")

        if (cityStateZip.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cityStateZip,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }

        // Phone number
        if (address.phoneNumber.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Phone: ${address.phoneNumber}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // Set as default action if not already default
        if (!isDefault) {
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = onSetDefault,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(R.color.orange)
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "✓ Set as default address",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AddressEditorDialog(
    initialAddress: SavedAddressModel?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        street: String,
        city: String,
        state: String,
        zip: String,
        phone: String,
        isDefault: Boolean
    ) -> Unit
) {
    val isEdit = initialAddress != null

    var title by remember { mutableStateOf(initialAddress?.title ?: "Home") }
    var street by remember { mutableStateOf(initialAddress?.street.orEmpty()) }
    var city by remember { mutableStateOf(initialAddress?.city.orEmpty()) }
    var state by remember { mutableStateOf(initialAddress?.state.orEmpty()) }
    var zip by remember { mutableStateOf(initialAddress?.zipCode.orEmpty()) }
    var phone by remember { mutableStateOf(initialAddress?.phoneNumber.orEmpty()) }
    var isDefault by remember { mutableStateOf(initialAddress?.isDefault ?: false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val tags = listOf("Home", "Work", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorResource(R.color.black3),
        title = {
            Text(
                text = if (isEdit) "Edit Address" else "Add New Address",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tag selector
                Text(
                    text = "Label",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        val isSelected = title.equals(tag, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) colorResource(R.color.orange)
                                    else colorResource(R.color.black2)
                                )
                                .clickable { title = tag }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp
                    )
                }

                // Street field
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it; errorMessage = null },
                    label = { Text("Street Address *") },
                    singleLine = true,
                    colors = addressDialogFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // City & State
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it; errorMessage = null },
                        label = { Text("City *") },
                        singleLine = true,
                        colors = addressDialogFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.2f)
                    )

                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        singleLine = true,
                        colors = addressDialogFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                // Zip & Phone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = zip,
                        onValueChange = { zip = it },
                        label = { Text("Zip Code") },
                        singleLine = true,
                        colors = addressDialogFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.9f)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; errorMessage = null },
                        label = { Text("Phone *") },
                        singleLine = true,
                        colors = addressDialogFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.1f)
                    )
                }

                // Set as default checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDefault = !isDefault }
                        .padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.orange),
                            checkmarkColor = Color.Black
                        )
                    )
                    Text(
                        text = "Set as default address",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (street.isBlank()) {
                        errorMessage = "Street address is required"
                        return@Button
                    }
                    if (city.isBlank()) {
                        errorMessage = "City is required"
                        return@Button
                    }
                    if (phone.isBlank()) {
                        errorMessage = "Phone number is required"
                        return@Button
                    }
                    onSave(title, street, city, state, zip, phone, isDefault)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.orange)
                )
            ) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        }
    )
}

@Composable
private fun addressDialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = colorResource(R.color.orange),
    unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
    focusedLabelColor = colorResource(R.color.orange),
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
    cursorColor = colorResource(R.color.orange)
)
