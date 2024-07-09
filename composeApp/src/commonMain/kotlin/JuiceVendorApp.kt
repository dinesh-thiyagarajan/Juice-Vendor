import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import composables.AddNewJuiceComposable
import composables.JuiceListComposable
import composables.ReportsComposable
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_refresh
import juicevendor.composeapp.generated.resources.ic_report
import juicevendor.composeapp.generated.resources.juice_preparation
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import viewModels.JuiceVendorViewModel

@Composable
@Preview
fun JuiceVendorApp(juiceVendorViewModel: JuiceVendorViewModel) {
    val showAddJuiceComposable = juiceVendorViewModel.showAddJuiceComposable.collectAsState()
    val showReportsComposable = juiceVendorViewModel.showReportsComposable.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    MaterialTheme {
        Column {
            if (showAddJuiceComposable.value) {
                AddNewJuiceComposable(juiceVendorViewModel = juiceVendorViewModel)
            } else if (showReportsComposable.value) {
                ReportsComposable(juiceVendorViewModel = juiceVendorViewModel)
            } else {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Image(
                            painter = painterResource(Res.drawable.ic_refresh),
                            contentDescription = "refresh juice orders",
                            modifier = Modifier.size(30.dp).clickable {
                                coroutineScope.launch {
                                    juiceVendorViewModel.getDrinkOrders()
                                }
                            },
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Image(
                            painter = painterResource(Res.drawable.ic_report),
                            contentDescription = "see reports",
                            modifier = Modifier.size(30.dp).clickable {
                                coroutineScope.launch {
                                    juiceVendorViewModel.updateReportsComposableVisibility(status = true)
                                }
                            },
                            contentScale = ContentScale.Fit
                        )
                    }
                    Image(
                        painter = painterResource(Res.drawable.juice_preparation),
                        contentDescription = "update juices list",
                        modifier = Modifier.size(30.dp).clickable {
                            juiceVendorViewModel.updateAddJuiceComposableVisibility(status = true)
                        },
                        contentScale = ContentScale.Fit
                    )
                }
                JuiceListComposable(juiceVendorViewModel)
            }
        }
    }
}