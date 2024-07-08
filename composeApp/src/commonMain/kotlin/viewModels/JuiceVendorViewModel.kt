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
        }
    }

    suspend fun addNewDrink(drink: Drink) {
        viewModelScope.launch(Dispatchers.IO) {
            juiceVendorRepository.addNewDrink(drink = drink, collection = JUICE_LIST_COLLECTION)
        }
    }

}