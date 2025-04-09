package com.example.ass2_travaler

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.ass2_travaler.data.BudgetItem
import com.example.ass2_travaler.data.BudgetRepository
import com.example.ass2_travaler.data.TravelPlan
import com.example.ass2_travaler.data.TravelPlanRepository
import com.example.ass2_travaler.data.TravelRepository
import com.example.ass2_travaler.model.City
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import com.example.ass2_travaler.viewmodel.HomeCityViewModel.CityUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


object FakeSharedTravelData {
    val cities = MutableStateFlow<List<City>>(emptyList())
}


object SharedTravelData {
    var cities = FakeSharedTravelData.cities
}


fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            data = t
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    afterObserve.invoke()
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData 值未更新。")
    }
    return data as T
}


class FakeTravelRepository : TravelRepository {
    override suspend fun getCities(): List<City> {

        return FakeSharedTravelData.cities.value
    }

    override fun getCityById(cityId: String, cities: List<City>): City? {
        return cities.find { it.id == cityId }
    }
}


class FakeTravelPlanRepository : TravelPlanRepository {
    private val travelPlansFlow = MutableStateFlow<List<TravelPlan>>(emptyList())
    override fun getTravelPlans(): Flow<List<TravelPlan>> = travelPlansFlow

    override suspend fun insertTravelPlan(plan: TravelPlan): Long {
        travelPlansFlow.value = travelPlansFlow.value + plan
        return 1L
    }

    override suspend fun updateTravelPlan(plan: TravelPlan): Int {
        travelPlansFlow.value = travelPlansFlow.value.map { if (it.id == plan.id) plan else it }
        return 1
    }

    override suspend fun deleteTravelPlan(plan: TravelPlan): Int {
        travelPlansFlow.value = travelPlansFlow.value.filter { it.id != plan.id }
        return 1
    }
}


class FakeBudgetRepository : BudgetRepository {
    private val itemsFlow = MutableStateFlow<List<BudgetItem>>(emptyList())
    override val totalSpending = itemsFlow.map { list -> list.sumOf { it.amount } }
    override fun getAll(): Flow<List<BudgetItem>> = itemsFlow

    override suspend fun insert(item: BudgetItem): Long {
        itemsFlow.value = itemsFlow.value + item
        return 1L
    }

    override suspend fun update(item: BudgetItem) {
        itemsFlow.value = itemsFlow.value.map { if (it.id == item.id) item else it }
    }

    override suspend fun delete(item: BudgetItem) {
        itemsFlow.value = itemsFlow.value.filter { it.id != item.id }
    }
}

//if you want to test the viewmodel, please use TestHomeCityViewModel:
//class TestHomeCityViewModel(
//    cityRepository: TravelRepository,
//    travelPlanRepository: TravelPlanRepository,
//    budgetRepository: BudgetRepository
//) : HomeCityViewModel(cityRepository, travelPlanRepository, budgetRepository) {
//    // Override init so that infinite collection is taken only once, avoiding infinite waiting for runTest
//    init {
//        viewModelScope.launch {
//            SharedTravelData.cities
//                .combine(selectedId) { cities, id -> cities to id }
//                .take(1)
//                .collect { (cities, id) ->
//                    handleCitySelection(cities, id)
//                }
//        }
//    }
//}
// Unit tests: for each function in the ViewModel

@ExperimentalCoroutinesApi
class HomeCityViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeTravelRepository: FakeTravelRepository
    private lateinit var fakeTravelPlanRepository: FakeTravelPlanRepository
    private lateinit var fakeBudgetRepository: FakeBudgetRepository

    private lateinit var viewModel: HomeCityViewModel //if your if you want to test the viewmodel, please change this one
    //if you want to test the viewmodel, please use TestHomeCityViewModel:
//     private lateinit var viewModel: TestHomeCityViewModel

    @Before
    fun setup() {
        // Construct a Fake repository
        Dispatchers.setMain(StandardTestDispatcher())
        fakeTravelRepository = FakeTravelRepository()
        fakeTravelPlanRepository = FakeTravelPlanRepository()
        fakeBudgetRepository = FakeBudgetRepository()

        // Set the test city list
        FakeSharedTravelData.cities.value = listOf(
            City(id = "1", title = "Test City One", top_attractions = "Test Attraction One", optimal_budget_usd = 100, rating = 5, best_season = "Spring", imageUrl = listOf("https://plus.unsplash.com/premium_photo-1661964011313-124f788fcb65?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8aG9uZ2tvbmd8ZW58MHx8MHx8fDA%3D")),
            City(id = "2", title = "Test City Two",top_attractions = "Test Attraction Two", optimal_budget_usd = 200, rating = 3, best_season = "None", imageUrl = listOf("https://upload.wikimedia.org/wikipedia/commons/4/46/Melburnian_Skyline_b.jpg"))
        )
        // Point the global SharedTravelData to the Flow for testing
        SharedTravelData.cities = FakeSharedTravelData.cities

        // Create the ViewModel to be tested
        ////if your if you want to test the viewmodel, please change this one
        viewModel = HomeCityViewModel(
            cityRepository = fakeTravelRepository,
            travelPlanRepository = fakeTravelPlanRepository,
            budgetRepository = fakeBudgetRepository
        )
        // //if you want to test the viewmodel, please use TestHomeCityViewModel:
//        viewModel = TestHomeCityViewModel(
//            cityRepository = fakeTravelRepository,
//            travelPlanRepository = fakeTravelPlanRepository,
//            budgetRepository = fakeBudgetRepository
//        )
    }
    @After
    fun tearDown() = kotlinx.coroutines.runBlocking {
        // Cancel all started coroutines inside the ViewModel
        viewModel.viewModelScope.cancel()
        testDispatcher.scheduler.advanceUntilIdle()
        Dispatchers.resetMain()
    }
    @Test
    fun testSetCityId_valid() = runTest {
        viewModel.setCityId("1")

        val uiState = viewModel.uiState.first { it is CityUiState.Success || it is CityUiState.Error }

        assertTrue(uiState is CityUiState.Success)
        if (uiState is CityUiState.Success) {
            assertEquals("Test City One", uiState.city.title)
        }
    }

    @Test
    fun testSetCityId_invalid() = runTest {
        viewModel.setCityId("99")

        val uiState = viewModel.uiState.first { it is CityUiState.Success || it is CityUiState.Error }

        assertTrue(uiState is CityUiState.Error)
        if (uiState is CityUiState.Error) {
            assertEquals("City not found", uiState.message)
        }
    }
    // Test setBudgetLimit: Check that the budget limit is updated correctly
    @Test
    fun testSetBudgetLimit()  {
        viewModel.setBudgetLimit(150.0)
        assertEquals(150.0, viewModel.budgetLimit.value)
    }

    // Test addPlan: Add a trip plan
    @Test
    fun testAddPlan() = runTest {
        val plan = TravelPlan(id = "plan1", eventName = "Trip to Test City", dateTime = 0, location = "Test Plan")
        viewModel.addPlan(plan)
        advanceUntilIdle()
        val plans = fakeTravelPlanRepository.getTravelPlans().first()
        assertTrue(plans.any { it.id == "plan1" })
    }

    // Test updatePlan: Updates the trip plan
    @Test
    fun testUpdatePlan() = runTest {
        val plan = TravelPlan(id = "plan2", eventName = "Original Trip",dateTime = 1, location = "Test Plan2")
        viewModel.addPlan(plan)

        val updatedPlan = plan.copy(eventName = "Updated Trip")
        viewModel.updatePlan(updatedPlan)
        advanceUntilIdle()
        val plans = fakeTravelPlanRepository.getTravelPlans().first()
        val planInRepo = plans.find { it.id == "plan2" }
        assertNotNull(planInRepo)
        assertEquals("Updated Trip", planInRepo?.eventName)
    }

    // Test deletePlan: Deletes a trip plan
    @Test
    fun testDeletePlan() = runTest {
        val plan = TravelPlan(id = "plan3", eventName = "Plan to Delete", dateTime = 2, location = "Test Plan3")
        viewModel.addPlan(plan)
        viewModel.deletePlan(plan)
        advanceUntilIdle()
        val plans = fakeTravelPlanRepository.getTravelPlans().first()
        assertFalse(plans.any { it.id == "plan3" })
    }

    // Test addItem: Add a budget item
    @Test
    fun testAddItem() = runTest {
        val item = BudgetItem(id = 1L, category = "Food", amount = 25.0, createdAt = "2023-04-01")
        viewModel.addItem(item)
        advanceUntilIdle()
        val items = fakeBudgetRepository.getAll().first()
        assertTrue(items.any { it.id == 1L })
    }

    // Test updateItem: Updates the budget item
    @Test
    fun testUpdateItem() = runTest {
        val item = BudgetItem(id = 2L, category = "Transport", amount = 10.0, createdAt = "2023-04-02")
        viewModel.addItem(item)
        val updatedItem = item.copy(amount = 15.0)
        viewModel.updateItem(updatedItem)
        advanceUntilIdle()
        val items = fakeBudgetRepository.getAll().first()
        val itemInRepo = items.find { it.id == 2L }
        assertNotNull(itemInRepo)
        assertEquals(15.0, itemInRepo?.amount)
    }

    // Test deleteItem: Deletes a budget item
    @Test
    fun testDeleteItem() = runTest {
        val item = BudgetItem(id = 3L, category = "Lodging", amount = 100.0, createdAt = "2023-04-03")
        viewModel.addItem(item)
        viewModel.deleteItem(item)
        advanceUntilIdle()
        val items = fakeBudgetRepository.getAll().first()
        assertFalse(items.any { it.id == 3L })
    }
}