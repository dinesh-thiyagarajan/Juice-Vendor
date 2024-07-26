package auth.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import auth.viewModels.AuthViewModel
import common.composables.TopAppBarComposable
import data.Role
import data.User
import juices.viewModels.JuiceVendorViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddNewUserComposable(juiceVendorViewModel: JuiceVendorViewModel, authViewModel: AuthViewModel) {

    val coroutineScope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBarComposable(onCloseButtonClicked = {
            juiceVendorViewModel.updateAddNewUserComposableVisibility(status = false)
        })
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
        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val user = User(
                            id = UUID.randomUUID().toString(),
                            email = userEmail,
                            name = userName,
                            role = Role.ADMIN
                        )
                        authViewModel.addNewUser(user)
                        juiceVendorViewModel.updateAddNewUserComposableVisibility(status = false)
                    }
                },
                enabled = userName.isNotEmpty() && userEmail.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Create Admin", maxLines = 1)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.align(Alignment.Start)) {
            Text(
                "Admins", style = MaterialTheme.typography.h4,
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
        UsersListComposable(authViewModel = authViewModel, coroutineScope = coroutineScope)
    }

}