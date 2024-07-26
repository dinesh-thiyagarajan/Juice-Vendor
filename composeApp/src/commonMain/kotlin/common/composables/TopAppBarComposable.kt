package common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun TopAppBarComposable(onCloseButtonClicked: () -> Unit) {
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
            modifier = Modifier.size(30.dp).clickable {
                onCloseButtonClicked.invoke()
            },
            contentScale = ContentScale.Fit
        )
    }
}