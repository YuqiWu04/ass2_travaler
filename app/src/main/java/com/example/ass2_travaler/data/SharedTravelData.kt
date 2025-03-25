import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ass2_travaler.TravalerApplication

import com.example.ass2_travaler.data.TravelRepository
import com.example.ass2_travaler.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object SharedTravelData {
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()

    private var isDataLoaded = false  // Prevent duplicate loading

    suspend fun initialize(application: Application) {
        val cityRepository = (application as TravalerApplication).container.cityRepository
        fetchCities(cityRepository)
    }

    private suspend fun fetchCities(cityRepository: TravelRepository) {
        if (isDataLoaded) return

        // If getCities() is a pending function, it automatically switches the context of the coprocessor.
        _cities.value = cityRepository.getCities()
        isDataLoaded = true
    }
}