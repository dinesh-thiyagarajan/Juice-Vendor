package juices.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Order
import juices.viewModels.JuiceVendorViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersListComposable(juiceVendorViewModel: JuiceVendorViewModel) {

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(juiceVendorViewModel) {
        coroutineScope.launch {
            juiceVendorViewModel.refreshDrinkOrdersWithAutoTimeInterval()
        }
    }

    val drinks = juiceVendorViewModel.orders.collectAsState()
    val totalOrdersCount = juiceVendorViewModel.totalOrdersCount.collectAsState()
    val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Total number of orders for today ${formatter.format(Date())}")
        Text(
            "${totalOrdersCount.value}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.h2,
            color = Color.Black,
            fontSize = 30.sp
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(drinks.value.size) { index ->
                JuiceItem(order = drinks.value[index])
            }
        }
    }
}


@Composable
fun JuiceItem(order: Order) {
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
                text = order.drinkName,
                modifier = Modifier.weight(1f)
            )
            Text(text = order.orderCount.toString())
        }
    }
}