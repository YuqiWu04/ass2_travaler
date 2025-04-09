package com.example.ass2_travaler.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ass2_travaler.TravalerApplication
import com.example.ass2_travaler.data.BudgetItem
import com.example.ass2_travaler.data.BudgetRepository
import com.example.ass2_travaler.data.TravelPlan
import com.example.ass2_travaler.data.TravelPlanRepository
import com.example.ass2_travaler.data.TravelRepository
import com.example.ass2_travaler.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import androidx.lifecycle.map
//  if you want to text for unit test ,please use open
//open
class HomeCityViewModel(private val cityRepository: TravelRepository, private val travelPlanRepository: TravelPlanRepository, private val budgetRepository: BudgetRepository) : ViewModel() {

    // Managing Selected IDs with MutableStateFlow
    private val _selectedId = MutableStateFlow<String>("")
    val selectedId: StateFlow<String> = _selectedId.asStateFlow()

    // Managing UI State with Sealed Classes
    private val _uiState = MutableStateFlow<CityUiState>(CityUiState.Loading)
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()
    val travelPlans = travelPlanRepository.getTravelPlans().asLiveData()

    val items: LiveData<List<BudgetItem>> = budgetRepository.getAll().asLiveData()
    val totalSpending = budgetRepository.totalSpending.asLiveData()
    private val _budgetLimit = MutableLiveData(0.0)
    private val _items = MutableLiveData<List<BudgetItem>>(emptyList())

    val budgetLimit: LiveData<Double> = _budgetLimit
    //group calculate the total amount
    val aggregatedBudgetData: LiveData<List<Pair<String, Double>>> = items.map { budgetItems ->
        budgetItems.groupBy { it.category }
            .map { (category, items) -> category to items.sumOf { it.amount } }
            .sortedBy { it.first }
    }
    fun addPlan(plan: TravelPlan) {
        viewModelScope.launch {
            travelPlanRepository.insertTravelPlan(plan)
        }
    }

    fun updatePlan(plan: TravelPlan) {
        viewModelScope.launch {
            travelPlanRepository.updateTravelPlan(plan)
        }
    }
    fun deletePlan(plan: TravelPlan) {
        viewModelScope.launch {
            travelPlanRepository.deleteTravelPlan(plan)
        }
    }
    init {
        viewModelScope.launch {
            SharedTravelData.cities
                .combine(selectedId) { cities, id ->
                    // Returns the data pairs to be processed
                    cities to id
                }
                .collect { (cities, id) ->
                    // Processing data in a collect lambda
                    handleCitySelection(cities, id)
                }

        }
    }

    fun setCityId(cityId: String) {
        _selectedId.value = cityId
    }

    fun setBudgetLimit(limit: Double) {
        _budgetLimit.value = limit
    }

    fun addItem(item: BudgetItem) = viewModelScope.launch {
        budgetRepository.insert(item)
    }

    fun updateItem(updatedItem: BudgetItem) {
        viewModelScope.launch {
            budgetRepository.update(updatedItem)
            _items.value = _items.value?.map { item ->
                if (item.id == updatedItem.id) updatedItem else item
            }
        }
    }

    fun deleteItem(item: BudgetItem) = viewModelScope.launch {
        budgetRepository.delete(item)
    }
//if you want to test for unit test ,please use protected
    private fun handleCitySelection(cities: List<City>, id: String) {
        when {
            // Data not yet loaded
            cities.isEmpty() -> _uiState.value = CityUiState.Loading

            // Find city when there is a valid checkbox ID
            id.isNotBlank() -> {
                cityRepository.getCityById(id, cities)?.let { city ->
                    _uiState.value = CityUiState.Success(city)
                } ?: run {
                    _uiState.value = CityUiState.Error("City not found")
                }
            }

            // Default unchecked state
            else -> _uiState.value = CityUiState.Empty
        }
    }


    sealed class CityUiState {
        object Loading : CityUiState()
        object Empty : CityUiState()
        data class Success(val city: City) : CityUiState()
        data class Error(val message: String) : CityUiState()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as TravalerApplication)
                HomeCityViewModel(
                    cityRepository = application.container.cityRepository,
                    travelPlanRepository = application.container.travelPlanRepository,
                    budgetRepository = application.container.budgetRepository,
                )
            }
        }
    }
}