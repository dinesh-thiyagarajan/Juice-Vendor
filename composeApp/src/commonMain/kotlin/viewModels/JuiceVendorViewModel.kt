package viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Drink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import repositories.JuiceVendorRepository

class JuiceVendorViewModel(private val juiceVendorRepository: JuiceVendorRepository) : ViewModel() {

    val drinks: StateFlow<List<Drink>> get() = _drinks
    private val _drinks: MutableStateFlow<List<Drink>> = MutableStateFlow(
        listOf()
    )

    init {
        viewModelScope.launch {
           getDrinksList()
        }
    }

    suspend fun getDrinksList() {
        viewModelScope.launch {
            _drinks.value = juiceVendorRepository.getDrinkOrders()
        }
    }



}