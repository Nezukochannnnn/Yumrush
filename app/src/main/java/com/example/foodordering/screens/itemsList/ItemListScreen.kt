package com.example.foodordering.screens.itemsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodordering.R
import com.example.foodordering.domain.FoodModel
import com.example.foodordering.domain.UiState
import com.example.foodordering.screens.common.EmptySection
import com.example.foodordering.screens.common.ErrorSection
import com.example.foodordering.screens.common.FoodItemCardShimmer
import com.example.foodordering.viewModel.MainViewModel

@Composable
fun ItemListScreen(
    title: String?,
    id: String?,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onOpenDetail: (FoodModel) -> Unit,
    onAddToCart: (FoodModel) -> Unit = {}
) {
    val filteredState by viewModel.filteredFoodState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        viewModel.loadFiltered(id)
    }

    Scaffold(
        containerColor = colorResource(R.color.black2)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
            val (backBtn, cartTxt) = createRefs()
            if (title != null) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(ref = cartTxt) {
                            centerTo(parent)
                        },
                    textAlign = TextAlign.Center,
                    text = title,
                    fontSize = 25.sp,
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.Bold
                )
            }
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(ref = backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }

        // Search in category bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Search in $title...",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "✕",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { searchQuery = "" }
                            .padding(8.dp)
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = colorResource(R.color.black3),
                unfocusedContainerColor = colorResource(R.color.black3),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .height(46.dp)
        )

        when (val state = filteredState) {
            is UiState.Loading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(6) {
                        FoodItemCardShimmer()
                    }
                }
            }
            is UiState.Error -> {
                ErrorSection(
                    message = state.message,
                    onRetry = { viewModel.retryFiltered() }
                )
            }
            is UiState.Empty -> {
                EmptySection(
                    message = "No items found in this category"
                )
            }
            is UiState.Success -> {
                val displayedItems = if (searchQuery.isNotBlank()) {
                    val q = searchQuery.trim().lowercase()
                    state.data.filter {
                        it.Title.lowercase().contains(q) || it.Description.lowercase().contains(q)
                    }
                } else {
                    state.data
                }

                if (displayedItems.isEmpty()) {
                    EmptySection(message = "No items matching \"$searchQuery\"")
                } else {
                    ItemsList(
                        items = displayedItems,
                        onItemsClick = { food -> onOpenDetail(food) },
                        onAddToCart = onAddToCart
                    )
                }
            }
        }
    }
}
}