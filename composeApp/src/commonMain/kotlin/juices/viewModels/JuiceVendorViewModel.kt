package juices.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Drink
import data.Order
import data.Report
import data.Status
import juices.repositories.JuiceVendorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val JUICE_LIST_COLLECTION = "Juices"
const val JUICE_REFRESH_TIME_IN_MS = 5000L

class JuiceVendorViewModel(private val juiceVendorRepository: JuiceVendorRepository) : ViewModel() {

    val orders: StateFlow<List<Order>> get() = _orders
    private val _orders: MutableStateFlow<List<Order>> = MutableStateFlow(
        listOf()
    )

    val drinksUiState: StateFlow<DrinksUiState> get() = _drinksUiState
    private val _drinksUiState: MutableStateFlow<DrinksUiState> = MutableStateFlow(
        DrinksUiState.Loading
    )

    private val drinksList: MutableStateFlow<MutableList<Drink>> =
        MutableStateFlow(mutableListOf())

    val totalOrdersCount: StateFlow<Int> get() = _totalOrdersCount
    private val _totalOrdersCount: MutableStateFlow<Int> = MutableStateFlow(0)

    val reportMap: StateFlow<HashMap<String, Report>> get() = _reportMap
    private val _reportMap: MutableStateFlow<HashMap<String, Report>> = MutableStateFlow(
        hashMapOf()
    )

    val showAddJuiceComposable: StateFlow<Boolean> get() = _showAddJuiceComposable
    private val _showAddJuiceComposable: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    val showReportsComposable: StateFlow<Boolean> get() = _showReportsComposable
    private val _showReportsComposable: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    fun updateAddJuiceComposableVisibility(status: Boolean) {
        _showAddJuiceComposable.value = status
    }

    fun updateReportsComposableVisibility(status: Boolean) {
        _showReportsComposable.value = status
    }

    suspend fun refreshDrinkOrdersWithAutoTimeInterval() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                getDrinkOrders()
                delay(JUICE_REFRESH_TIME_IN_MS)
            }
        }
    }

    fun onJuiceAvailabilityUpdated(availability: Boolean, drinkId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedList = drinksList.value.map { drink ->
                if (drink.drinkId == drinkId) {
                    drink.copy(isAvailable = availability)
                } else {
                    drink
                }
            }
            drinksList.value = updatedList.toMutableList()
            _drinksUiState.value = DrinksUiState.Success(drinks = updatedList)
        }
    }

    suspend fun getDrinkOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            juiceVendorRepository.getDrinkOrders().data?.reversed()?.let {
                _orders.value = it
                calculateTotalOrdersCount(it)
            }
        }
    }

    private fun calculateTotalOrdersCount(orders: List<Order>) {
        var orderCount = 0
        orders.forEach { drink ->
            orderCount += drink.orderCount
        }
        _totalOrdersCount.value = orderCount
    }

    suspend fun addNewDrink(drink: Drink) {
        viewModelScope.launch(Dispatchers.IO) {
            juiceVendorRepository.addNewDrink(drink = drink, collection = JUICE_LIST_COLLECTION)
        }
    }

    fun createOrdersAggregateReport() {
        viewModelScope.launch(Dispatchers.Default) {
            val reportMap: HashMap<String, Report> = hashMapOf()

            for (drink in orders.value) {
                val currentReport = reportMap[drink.drinkId]
                if (currentReport == null) {
                    // If no report exists for this drinkId, create a new Report object
                    reportMap[drink.drinkId] = Report(
                        drinkId = drink.drinkId,
                        drinkName = drink.drinkName,
                        orderCount = drink.orderCount,
                        drinkImage = drink.drinkImage
                    )
                } else {
                    // If a report already exists, update the order count
                    reportMap[drink.drinkId] = currentReport.copy(
                        orderCount = currentReport.orderCount + drink.orderCount
                    )
                }
            }
            _reportMap.value = reportMap
        }
    }

    suspend fun getDrinksList() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = juiceVendorRepository.getDrinksList(JUICE_LIST_COLLECTION)
            when (response.status) {
                Status.Loading -> {
                    _drinksUiState.value = DrinksUiState.Loading
                }

                Status.Success -> {
                    drinksList.value = response.data?.toMutableList() ?: mutableListOf()
                    _drinksUiState.value =
                        DrinksUiState.Success(
                            drinks = response.data?.toList() ?: emptyList()
                        )
                }

                Status.Error -> {
                    _drinksUiState.value = DrinksUiState.Error(message = "Unable to fetch Juices")
                }
            }
        }
    }
}

sealed interface DrinksUiState {
    data object Loading : DrinksUiState
    data class Success(val drinks: List<Drink>) : DrinksUiState
    data class Error(val message: String?) : DrinksUiState
}