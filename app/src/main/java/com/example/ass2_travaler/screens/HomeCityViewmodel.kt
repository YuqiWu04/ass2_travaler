package com.example.ass2_travaler.screens

import SharedTravelData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ass2_travaler.TravalerApplication
import com.example.ass2_travaler.data.TravelRepository
import com.example.ass2_travaler.model.City


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

import kotlinx.coroutines.launch


class HomeCityViewModel(private val cityRepository: TravelRepository) : ViewModel() {

    // Managing Selected IDs with MutableStateFlow
    private val _selectedId = MutableStateFlow<String>("")
    val selectedId: StateFlow<String> = _selectedId.asStateFlow()

    // Managing UI State with Sealed Classes
    private val _uiState = MutableStateFlow<CityUiState>(CityUiState.Loading)
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()



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
                val application = (this[APPLICATION_KEY] as TravalerApplication)
                HomeCityViewModel(
                    cityRepository = application.container.cityRepository
                )
            }
        }
    }
}