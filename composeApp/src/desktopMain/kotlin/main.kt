import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import repositories.JuiceVendorRepository
import viewModels.JuiceVendorViewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Juice Vendor",
    ) {
        JuiceVendorApp(JuiceVendorViewModel(JuiceVendorRepository()))
    }
}