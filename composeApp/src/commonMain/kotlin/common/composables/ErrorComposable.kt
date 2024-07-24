package common.composables

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_404
import org.jetbrains.compose.resources.painterResource

@Composable
fun ErrorComposable(errorMessage: String = "Something went wrong while fetching the data, Please check your internet connection, if its fine please contact Admin") {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_404),
            contentDescription = "error",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = errorMessage,
            modifier = Modifier.padding(30.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}