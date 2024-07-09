package composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_apple
import juicevendor.composeapp.generated.resources.ic_close
import juicevendor.composeapp.generated.resources.ic_coffee
import juicevendor.composeapp.generated.resources.ic_fruit_bowl
import juicevendor.composeapp.generated.resources.ic_orange
import juicevendor.composeapp.generated.resources.ic_tea
import juicevendor.composeapp.generated.resources.ic_watermelon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import viewModels.JuiceVendorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ReportsComposable(juiceVendorViewModel: JuiceVendorViewModel) {

    val totalOrdersCount = juiceVendorViewModel.totalOrdersCount.collectAsState()
    val reportMap = juiceVendorViewModel.reportMap.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(juiceVendorViewModel) {
        coroutineScope.launch {
            juiceVendorViewModel.createOrdersAggregateReport()
        }
    }

    val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
    Column(modifier = Modifier.padding(20.dp)) {
        Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(30.dp).clickable {
                    juiceVendorViewModel.updateReportsComposableVisibility(status = false)
                },
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Total number of orders for date ${formatter.format(Date())}")
        Text(
            "${totalOrdersCount.value}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.h2,
            color = Color.Black,
            fontSize = 30.sp
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            reportMap.value.forEach { map ->
                item {
                    RoundedCardView(
                        juiceName = map.value.drinkName,
                        imageId = map.value.drinkImage,
                        orderCount = map.value.orderCount
                    )
                }
            }
        }
    }
}

private fun getResourceDrawable(imageId: String): DrawableResource {
    return when (imageId.toLowerCase(androidx.compose.ui.text.intl.Locale.current)) {
        "orange" -> Res.drawable.ic_orange
        "apple" -> Res.drawable.ic_apple
        "watermelon" -> Res.drawable.ic_watermelon
        "fruitbowl" -> Res.drawable.ic_fruit_bowl
        "tea" -> Res.drawable.ic_tea
        "coffee" -> Res.drawable.ic_coffee
        else -> Res.drawable.ic_orange
    }
}

@Composable
fun RoundedCardView(
    juiceName: String,
    imageId: String,
    orderCount: Int
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 10.dp).fillMaxWidth()) {

            Image(
                painter = painterResource(getResourceDrawable(imageId)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = juiceName,
                style = MaterialTheme.typography.h4,
                color = Color.Black,
                fontSize = 18.sp
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = orderCount.toString(),
                style = MaterialTheme.typography.h2,
                color = Color.Black,
                fontSize = 30.sp
            )

        }
    }
}