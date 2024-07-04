import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import composables.AddRemoveJuiceComposable
import composables.JuiceListComposable
import org.jetbrains.compose.ui.tooling.preview.Preview
import viewModels.JuiceVendorViewModel

@Composable
@Preview
fun JuiceVendorApp(juiceKadaiViewModel: JuiceVendorViewModel) {
    MaterialTheme {
        JuiceListComposable(juiceKadaiViewModel)
        AddRemoveJuiceComposable()
    }
}