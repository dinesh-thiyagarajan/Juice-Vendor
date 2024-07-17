package auth.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import auth.viewModels.AuthViewModel
import data.Role
import data.User
import juices.viewModels.JuiceVendorViewModel
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_close
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.util.UUID

@Composable
fun AddNewUserComposable(juiceVendorViewModel: JuiceVendorViewModel, authViewModel: AuthViewModel) {

    val coroutineScope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(30.dp).clickable {
                    juiceVendorViewModel.updateAddNewUserComposableVisibility(status = false)
                },
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = userName,
            isError = userName.isEmpty(),
            onValueChange = { userName = it },
            label = { Text("Please enter user name") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userEmail,
            isError = userEmail.isEmpty(),
            onValueChange = { userEmail = it },
            label = { Text("Please enter valid email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Do you want to make him an Admin")
        Spacer(modifier = Modifier.height(5.dp))
        Switch(
            checked = isAdmin,
            onCheckedChange = { isAdmin = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.onSurface
            )
        )
        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val user = User(
                            id = UUID.randomUUID().toString(),
                            email = userEmail,
                            name = userName,
                            role = if (isAdmin) Role.ADMIN else Role.VENDOR
                        )
                        authViewModel.addNewUser(user)
                        juiceVendorViewModel.updateAddNewUserComposableVisibility(status = false)
                    }
                },
                enabled = userName.isNotEmpty() && userEmail.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                Text("Save")
            }
        }
        UsersListComposable(authViewModel = authViewModel, coroutineScope = coroutineScope)
    }

}