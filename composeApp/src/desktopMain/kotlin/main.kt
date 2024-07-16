import android.app.Application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import auth.repositories.AuthRepository
import auth.viewModels.AuthViewModel
import com.google.firebase.FirebasePlatform
import data.Config
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import juices.repositories.JuiceVendorRepository
import juices.viewModels.JuiceVendorViewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Juice Vendor",
    ) {

        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {

            val storage = mutableMapOf<String, String>()
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)

            override fun retrieve(key: String) = storage[key]

            override fun store(key: String, value: String) = storage.set(key, value)

        })

        val options = FirebaseOptions(
            projectId = Config.PROJECT_ID,
            applicationId = Config.APP_ID,
            apiKey = Config.API_KEY,
            databaseUrl = Config.FIREBASE_DB_URL
        )

        Firebase.initialize(Application(), options)

        JuiceVendorApp(JuiceVendorViewModel(JuiceVendorRepository()), AuthViewModel(AuthRepository()))
    }
}