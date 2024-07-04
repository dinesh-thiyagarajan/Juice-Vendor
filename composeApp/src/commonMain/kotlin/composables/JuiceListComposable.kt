package composables

import viewModels.JuiceVendorViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Drink

@Composable
fun JuiceListComposable(juiceVendorViewModel: JuiceVendorViewModel) {
    val drinks = juiceVendorViewModel.drinks.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(drinks.value.size) { index ->
            JuiceItem(drink = drinks.value[index])
        }
    }
}


@Composable
fun JuiceItem(drink: Drink) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = drink.drinkName,
                modifier = Modifier.weight(1f)
            )
            Text(text = drink.itemCount.toString())
        }
    }
}