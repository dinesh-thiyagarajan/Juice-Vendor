package auth.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import auth.viewModels.AuthViewModel
import auth.viewModels.GetUsersUiState
import common.composables.ErrorComposable
import common.composables.LoadingComposable
import data.User
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_delete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun UsersListComposable(authViewModel: AuthViewModel, coroutineScope: CoroutineScope) {

    LaunchedEffect(authViewModel) {
        coroutineScope.launch {
            authViewModel.getUsersList()
        }
    }

    val getUsersUiState = authViewModel.getUsersUiState.collectAsState()
    when (getUsersUiState.value) {
        is GetUsersUiState.Loading -> {
            LoadingComposable()
        }

        is GetUsersUiState.Success -> {
            val users = (getUsersUiState.value as GetUsersUiState.Success).usersList
            UpdateUsersListComposable(
                users = users,
                authViewModel = authViewModel,
                coroutineScope = coroutineScope
            )
        }

        is GetUsersUiState.Error -> {
            ErrorComposable()
        }
    }

}

@Composable
fun UpdateUsersListComposable(
    users: List<User>,
    authViewModel: AuthViewModel,
    coroutineScope: CoroutineScope
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(users.size) { index ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = users[index].email)
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = "delete user",
                    modifier = Modifier.size(30.dp).clickable {
                        coroutineScope.launch {
                            authViewModel.deleteUser(user = users[index])
                        }
                    },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}