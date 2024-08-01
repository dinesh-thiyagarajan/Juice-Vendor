import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import auth.composables.AddNewUserComposable
import auth.composables.FetchingLoginStatusComposable
import auth.composables.LoginComposable
import auth.viewModels.AuthUiState
import auth.viewModels.AuthViewModel
import common.theme.primary_wave_blue
import juices.composables.AddNewJuiceComposable
import juices.composables.OrdersListComposable
import juices.composables.ReportsComposable
import juices.viewModels.JuiceVendorViewModel
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_404
import juicevendor.composeapp.generated.resources.ic_add_user
import juicevendor.composeapp.generated.resources.ic_refresh
import juicevendor.composeapp.generated.resources.ic_report
import juicevendor.composeapp.generated.resources.juice_preparation
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun JuiceVendorApp(juiceVendorViewModel: JuiceVendorViewModel, authViewModel: AuthViewModel) {
    val showAddJuiceComposable = juiceVendorViewModel.showAddJuiceComposable.collectAsState()
    val showReportsComposable = juiceVendorViewModel.showReportsComposable.collectAsState()
    val showAddNewUserComposable = juiceVendorViewModel.showAddNewUserComposable.collectAsState()
    val authUiState = authViewModel.authUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    MaterialTheme {
        when (authUiState.value) {
            is AuthUiState.FetchingLoginStatus -> {
                FetchingLoginStatusComposable()
            }

            is AuthUiState.NotLoggedIn -> {
                LoginComposable(authViewModel = authViewModel, coroutineScope = coroutineScope)
            }

            is AuthUiState.LoggedIn -> {
                Column {
                    when {
                        showAddJuiceComposable.value -> {
                            AddNewJuiceComposable(
                                juiceVendorViewModel = juiceVendorViewModel,
                                (authUiState.value as AuthUiState.LoggedIn).isAdmin
                            )
                        }

                        showReportsComposable.value -> {
                            ReportsComposable(juiceVendorViewModel = juiceVendorViewModel)
                        }

                        showAddNewUserComposable.value -> {
                            AddNewUserComposable(
                                juiceVendorViewModel = juiceVendorViewModel,
                                authViewModel = authViewModel
                            )
                        }

                        else -> {
                            val isAdmin = (authUiState.value as AuthUiState.LoggedIn).isAdmin
                            Row(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Image(
                                        painter = painterResource(Res.drawable.ic_refresh),
                                        contentDescription = "refresh juice orders",

                                        modifier = Modifier.size(30.dp).clickable {
                                            coroutineScope.launch {
                                                juiceVendorViewModel.getDrinkOrders()
                                            }
                                        },
                                        contentScale = ContentScale.Fit
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Image(
                                        painter = painterResource(Res.drawable.ic_report),
                                        contentDescription = "see reports",
                                        modifier = Modifier.size(30.dp).clickable {
                                            coroutineScope.launch {
                                                juiceVendorViewModel.updateReportsComposableVisibility(
                                                    status = true
                                                )
                                            }
                                        },
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Row {
                                    if (isAdmin) {
                                        Image(
                                            painter = painterResource(Res.drawable.ic_add_user),
                                            contentDescription = "add user",
                                            modifier = Modifier.size(30.dp).clickable {
                                                juiceVendorViewModel.updateAddNewUserComposableVisibility(
                                                    status = true
                                                )
                                            },
                                            contentScale = ContentScale.Fit
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    Image(
                                        painter = painterResource(Res.drawable.juice_preparation),
                                        contentDescription = "update juices list",
                                        modifier = Modifier.size(30.dp).clickable {
                                            juiceVendorViewModel.updateAddJuiceComposableVisibility(
                                                status = true
                                            )
                                        },
                                        contentScale = ContentScale.Fit
                                    )
                                }

                            }
                            OrdersListComposable(juiceVendorViewModel)
                        }
                    }
                }
            }

            is AuthUiState.LoginInProgress -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = primary_wave_blue)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Logging You In")
                }
            }

            is AuthUiState.Error -> {
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
                        text = "Please contact admin reg login access",
                        modifier = Modifier.padding(30.dp),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.wrapContentWidth()
                            .padding(start = 10.dp),
                        onClick = {
                            coroutineScope.launch {
                                authViewModel.updateAuthUiState(AuthUiState.NotLoggedIn)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray,
                            contentColor = Color.White
                        ),
                    ) {
                        Text(text = "Go to Login", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}