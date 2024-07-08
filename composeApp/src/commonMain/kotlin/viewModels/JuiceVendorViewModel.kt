package viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Drink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import repositories.JuiceVendorRepository

const val JUICE_LIST_COLLECTION = "Juices"

class JuiceVendorViewModel(private val juiceVendorRepository: JuiceVendorRepository) : ViewModel() {

    val drinks: StateFlow<List<Drink>> get() = _drinks
    private val _drinks: MutableStateFlow<List<Drink>> = MutableStateFlow(
        listOf()
    )

    val totalOrdersCount: StateFlow<Int> get() = _totalOrdersCount
    private val _totalOrdersCount: MutableStateFlow<Int> = MutableStateFlow(0)

    /** TODO
     * Replace this with proper navigation and remove this
     **/
    val showAddJuiceComposable: StateFlow<Boolean> get() = _showAddJuiceComposable
    private val _showAddJuiceComposable: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    fun updateAddJuiceComposableVisibility(status: Boolean) {
        _showAddJuiceComposable.value = status
    }

    suspend fun getDrinkOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            _drinks.value = juiceVendorRepository.getDrinkOrders()
            calculateTotalOrdersCount(_drinks.value)
        }
    }

    private fun calculateTotalOrdersCount(drinks: List<Drink>) {
        var orderCount = 0
        drinks.forEach { drink ->
            orderCount += drink.orderCount
        }
        _totalOrdersCount.value = orderCount
    }

    suspend fun addNewDrink(drink: Drink) {
        viewModelScope.launch(Dispatchers.IO) {
            juiceVendorRepository.addNewDrink(drink = drink, collection = JUICE_LIST_COLLECTION)
        }
    }

}