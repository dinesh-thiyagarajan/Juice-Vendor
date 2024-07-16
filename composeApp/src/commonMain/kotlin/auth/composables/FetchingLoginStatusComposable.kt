package auth.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_report
import org.jetbrains.compose.resources.painterResource

@Composable
fun FetchingLoginStatusComposable() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painterResource(Res.drawable.ic_report),
            "check creds",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Please wait while we check your credentials",
            modifier = Modifier.padding(30.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))

    }

}