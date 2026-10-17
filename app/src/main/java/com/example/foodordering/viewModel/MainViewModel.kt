package com.example.foodordering.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodordering.Repository.MainRepository
import com.example.foodordering.domain.CategoryModel
import com.example.foodordering.domain.FoodFilter
import com.example.foodordering.domain.FoodModel
import com.example.foodordering.domain.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository = MainRepository()
) : ViewModel() {

    private val _rawCategoriesState = MutableStateFlow<UiState<List<CategoryModel>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<CategoryModel>>> = _rawCategoriesState.asStateFlow()

    private val _rawBestFoodState = MutableStateFlow<UiState<List<FoodModel>>>(UiState.Loading)
    val bestFoodState: StateFlow<UiState<List<FoodModel>>> = _rawBestFoodState.asStateFlow()

    private val _filteredFoodState = MutableStateFlow<UiState<List<FoodModel>>>(UiState.Loading)
    val filteredFoodState: StateFlow<UiState<List<FoodModel>>> = _filteredFoodState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FoodFilter.ALL)
    val selectedFilter: StateFlow<FoodFilter> = _selectedFilter.asStateFlow()

    private var currentFilterId: String? = null

    private val _selectedFood = MutableStateFlow<FoodModel?>(null)
    val selectedFood: StateFlow<FoodModel?> = _selectedFood.asStateFlow()

    val displayedFoodState: StateFlow<UiState<List<FoodModel>>> = combine(
        _rawBestFoodState,
        _searchQuery,
        _selectedFilter
    ) { foodUiState, query, filter ->
        when (foodUiState) {
            is UiState.Success -> {
                var list = foodUiState.data

                // Search query filter
                if (query.isNotBlank()) {
                    val q = query.trim().lowercase()
                    list = list.filter { food ->
                        food.Title.lowercase().contains(q) ||
                        food.Description.lowercase().contains(q)
                    }
                }

                // Filter chip criteria
                list = when (filter) {
                    FoodFilter.ALL -> list
                    FoodFilter.TOP_RATED -> list.filter { it.Star >= 4.5 }
                    FoodFilter.FAST_DELIVERY -> list.filter { it.TimeValue in 1..25 }
                    FoodFilter.UNDER_10 -> list.filter { it.Price in 0.01..9.99 }
                }

                if (list.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(list)
                }
            }
            else -> foodUiState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    val displayedCategoriesState: StateFlow<UiState<List<CategoryModel>>> = combine(
        _rawCategoriesState,
        _searchQuery
    ) { categoryUiState, query ->
        when (categoryUiState) {
            is UiState.Success -> {
                if (query.isBlank()) {
                    categoryUiState
                } else {
                    val q = query.trim().lowercase()
                    val filtered = categoryUiState.data.filter { it.Name.lowercase().contains(q) }
                    if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
                }
            }
            else -> categoryUiState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    init {
        loadCategories()
        loadBestFood()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _rawCategoriesState.value = UiState.Loading
            repository.loadCategory()
                .catch { e ->
                    _rawCategoriesState.value = UiState.Error(e.message ?: "Failed to load categories")
                }
                .collect { list ->
                    _rawCategoriesState.value = if (list.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(list)
                    }
                }
        }
    }

    fun loadBestFood() {
        viewModelScope.launch {
            _rawBestFoodState.value = UiState.Loading
            repository.loadBestFood()
                .catch { e ->
                    _rawBestFoodState.value = UiState.Error(e.message ?: "Failed to load foods")
                }
                .collect { list ->
                    _rawBestFoodState.value = if (list.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(list)
                    }
                }
        }
    }

    fun loadFiltered(id: String?) {
        currentFilterId = id
        viewModelScope.launch {
            _filteredFoodState.value = UiState.Loading
            repository.loadFilterFood(id)
                .catch { e ->
                    _filteredFoodState.value = UiState.Error(e.message ?: "Failed to load items")
                }
                .collect { list ->
                    _filteredFoodState.value = if (list.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(list)
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: FoodFilter) {
        _selectedFilter.value = filter
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedFilter.value = FoodFilter.ALL
    }

    fun retryFiltered() {
        loadFiltered(currentFilterId)
    }

    fun refresh() {
        loadCategories()
        loadBestFood()
    }

    fun selectedFood(foodModel: FoodModel?) {
        _selectedFood.value = foodModel
    }
}
