import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import composables.AddNewJuiceComposable
import composables.JuiceListComposable
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.juice_preparation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import viewModels.JuiceVendorViewModel

@Composable
@Preview
fun JuiceVendorApp(juiceVendorViewModel: JuiceVendorViewModel) {
    val showAddJuiceComposable = juiceVendorViewModel.showAddJuiceComposable.collectAsState()
    MaterialTheme {
        Column {
            if (showAddJuiceComposable.value) {
                AddNewJuiceComposable(juiceVendorViewModel = juiceVendorViewModel)
            } else {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
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