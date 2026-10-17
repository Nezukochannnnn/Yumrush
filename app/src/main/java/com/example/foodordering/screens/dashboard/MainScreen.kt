package com.example.foodordering.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.R
import com.example.foodordering.domain.FoodFilter
import com.example.foodordering.domain.FoodModel
import com.example.foodordering.domain.UiState
import com.example.foodordering.screens.common.CategoryShimmerRow
import com.example.foodordering.screens.common.EmptySection
import com.example.foodordering.screens.common.ErrorSection
import com.example.foodordering.screens.common.FoodItemCardShimmer
import com.example.foodordering.viewModel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    userEmail: String = "",
    onLogout: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenItems: (id: String, title: String) -> Unit,
    onOpenDetail: (FoodModel) -> Unit
) {
    val displayedCategoriesState by viewModel.displayedCategoriesState.collectAsState()
    val displayedFoodState by viewModel.displayedFoodState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        bottomBar = {
            MyBottomBar(
                selectedTab = "Home",
                onTabSelected = { tab ->
                    when (tab) {
                        "Profile" -> onNavigateToProfile()
                        "Cart" -> onNavigateToCart()
                        "Order" -> onNavigateToOrders()
                    }
                }
            )
        },
        containerColor = colorResource(R.color.black2)
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.black2))
                .padding(paddingValues),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                TopBar(
                    onProfileClick = onNavigateToProfile,
                    onRefreshClick = { viewModel.refresh() },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) }
                )
            }

            // Categories
            item(span = { GridItemSpan(2) }) {
                when (val state = displayedCategoriesState) {
                    is UiState.Loading -> {
                        CategoryShimmerRow()
                    }
                    is UiState.Error -> {
                        ErrorSection(
                            message = state.message,
                            onRetry = { viewModel.loadCategories() }
                        )
                    }
                    is UiState.Empty -> {
                        if (searchQuery.isNotEmpty()) {
                            // Don't clutter if just searching for foods
                        } else {
                            EmptySection(message = "No categories available")
                        }
                    }
                    is UiState.Success -> {
                        CategorySection(
                            categories = state.data,
                            showCategoryLoading = false,
                            onCategoryClick = { cat -> onOpenItems(cat.Id.toString(), cat.Name) }
                        )
                    }
                }
            }

            // Filter Chips Section
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FoodFilter.entries) { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) colorResource(R.color.orange) else colorResource(R.color.black3),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.onFilterSelected(filter) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter.title,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section Header
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results" else "Foods for you",
                    color = colorResource(R.color.white),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }

            // Food items / states
            when (val state = displayedFoodState) {
                is UiState.Loading -> {
                    items(6) {
                        FoodItemCardShimmer()
                    }
                }
                is UiState.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        ErrorSection(
                            message = state.message,
                            onRetry = { viewModel.loadBestFood() }
                        )
                    }
                }
                is UiState.Empty -> {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "No food items found",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No matches for \"$searchQuery\" with selected filter."
                                } else {
                                    "No foods available under this filter."
                                },
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = { viewModel.clearFilters() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.orange)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Reset Filters",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    items(state.data.size) { index ->
                        FoodItemCardGrid(
                            item = state.data[index],
                            onClick = {
                                onOpenDetail(state.data[index])
                            }
                        )
                    }
                }
            }
        }
    }
}
