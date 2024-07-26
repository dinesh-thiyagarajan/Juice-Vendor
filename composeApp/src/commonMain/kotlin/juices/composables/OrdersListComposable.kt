package juices.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Order
import juices.viewModels.JuiceVendorViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun OrdersListComposable(juiceVendorViewModel: JuiceVendorViewModel) {

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(juiceVendorViewModel) {
        coroutineScope.launch {
            juiceVendorViewModel.getDrinkOrders()
        }
    }

    val drinks = juiceVendorViewModel.orders.collectAsState()
    val totalOrdersCount = juiceVendorViewModel.totalOrdersCount.collectAsState()
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        val firstTextStyle = TextStyle(
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        val restTextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif
        )
        val orderText = "${totalOrdersCount.value} Orders for today"
        val annotatedString = buildAnnotatedString {
            withStyle(style = firstTextStyle.toSpanStyle()) {
                append(orderText.take(totalOrdersCount.value.toString().length))
            }
            withStyle(style = restTextStyle.toSpanStyle()) {
                append(orderText.drop(totalOrdersCount.value.toString().length))
            }
        }
        Text(
            text = annotatedString,
            textAlign = TextAlign.Justify
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
            .padding(vertical = 8.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(getResourceDrawable(order.drinkName)),
                contentDescription = "juiceImage",
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = order.drinkName,
                modifier = Modifier.weight(1f),
                style = TextStyle(fontSize = 17.sp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = order.orderCount.toString(),
                style = TextStyle(fontSize = 22.sp),
                textAlign = TextAlign.End
            )
        }
    }
}