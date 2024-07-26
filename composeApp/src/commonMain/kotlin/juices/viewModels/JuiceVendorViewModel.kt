package juices.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Drink
import data.Order
import data.Report
import data.Status
import file.FilesRepository
import file.Platform
import juices.repositories.JuiceVendorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JuiceVendorViewModel(
    private val juiceVendorRepository: JuiceVendorRepository,
    private val filesRepository: FilesRepository?,
) : ViewModel() {

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

    val generateReportUiState: StateFlow<GenerateReportUiState> get() = _generateReportUiState
    private val _generateReportUiState: MutableStateFlow<GenerateReportUiState> =
        MutableStateFlow(GenerateReportUiState.Loading)

    val exportReportUiState: StateFlow<ExportReportUiState> get() = _exportReportUiState
    private val _exportReportUiState: MutableStateFlow<ExportReportUiState> =
        MutableStateFlow(ExportReportUiState.NoUserActionState)

    val showAddJuiceComposable: StateFlow<Boolean> get() = _showAddJuiceComposable
    private val _showAddJuiceComposable: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    val showAddNewUserComposable: StateFlow<Boolean> get() = _showAddNewUserComposable
    private val _showAddNewUserComposable: MutableStateFlow<Boolean> = MutableStateFlow(
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

    fun updateAddNewUserComposableVisibility(status: Boolean) {
        _showAddNewUserComposable.value = status
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
            juiceVendorRepository.updateJuiceAvailability(
                drinkId = drinkId,
                availability = availability
            )
        }
    }

    suspend fun getDrinkOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            juiceVendorRepository.getDrinkOrders().collect { orders ->
                _orders.value = orders
                calculateTotalOrdersCount(orders)
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
            juiceVendorRepository.addNewDrink(drink = drink)
        }
    }

    // TODO Move these report related functions to separate Report ViewModel
    suspend fun onReportExportButtonClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            val reportHash =
                (_generateReportUiState.value as GenerateReportUiState.Success).reportsHashMap
            juiceVendorRepository.prepareJuiceDataForExport(reportHashMap = reportHash)
                .collect { fileContent ->
                    val generatedFile = filesRepository?.createCSVFile(fileContent = fileContent)
                    generatedFile?.let {
                        Platform.fileSharingService.shareFile(it)
                    }
                }
        }
    }


    suspend fun getReportForDateInterval(startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _generateReportUiState.value = GenerateReportUiState.Loading
            val reportMap: HashMap<String, Report> = hashMapOf()
            val datesList = getInBetweenDates(startDate = startDate, endDate = endDate)
            val reportResponse =
                juiceVendorRepository.getReportForDateInterval(datesList = datesList)
            when (reportResponse.status) {
                Status.Loading -> {
                    _generateReportUiState.value = GenerateReportUiState.Loading
                }

                Status.Success -> {
                    reportResponse.data?.second?.forEach { order ->
                        val currentReport = reportMap[order.drinkId]
                        if (currentReport == null) {
                            // If no report exists for this drinkId, create a new Report object
                            reportMap[order.drinkId] = Report(
                                drinkId = order.drinkId,
                                drinkName = order.drinkName,
                                orderCount = order.orderCount,
                                orderTimeStamp = order.orderTimeStamp
                            )
                        } else {
                            // If a report already exists, update the order count
                            reportMap[order.drinkId] = currentReport.copy(
                                orderCount = currentReport.orderCount + order.orderCount
                            )
                        }
                    }
                    _generateReportUiState.value = GenerateReportUiState.Success(
                        reportsHashMap = reportMap,
                        totalOrderCount = reportResponse.data?.first ?: 0
                    )
                }

                Status.Error -> {
                    _generateReportUiState.value =
                        GenerateReportUiState.Error(message = "Error while fetching the report")
                }
            }
        }
    }

    suspend fun getDrinksList() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = juiceVendorRepository.getDrinksList()
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

    private fun getInBetweenDates(startDate: String, endDate: String): List<String> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        val dates = mutableListOf<LocalDate>()
        var currentDate = start

        while (!currentDate.isAfter(end.plusDays(1))) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        return dates.map { it.format(formatter) }
    }
}

sealed interface DrinksUiState {
    data object Loading : DrinksUiState
    data class Success(val drinks: List<Drink>) : DrinksUiState
    data class Error(val message: String?) : DrinksUiState
}

sealed interface GenerateReportUiState {
    data object Loading : GenerateReportUiState
    data class Success(val reportsHashMap: HashMap<String, Report>, val totalOrderCount: Int) :
        GenerateReportUiState
    data class Error(val message: String?) : GenerateReportUiState
}

sealed interface ExportReportUiState {
    data object NoUserActionState : ExportReportUiState
    data object ExportInProgress : ExportReportUiState
    data class Success(val uri: Uri) : ExportReportUiState
    data class Error(val errorMsg: String) : ExportReportUiState
}