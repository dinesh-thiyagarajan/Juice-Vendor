package juices.composables

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Drink
import juices.viewModels.DrinksUiState
import juices.viewModels.JuiceVendorViewModel
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_404
import juicevendor.composeapp.generated.resources.ic_close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNewJuiceComposable(juiceVendorViewModel: JuiceVendorViewModel, isAdmin: Boolean) {
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    var juiceName by remember { mutableStateOf("") }
    var juiceImage by remember { mutableStateOf("") }
    var juiceAvailability by remember { mutableStateOf(true) }

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
                    juiceVendorViewModel.updateAddJuiceComposableVisibility(status = false)
                },
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (isAdmin) {
            OutlinedTextField(
                value = juiceName,
                isError = juiceName.isEmpty(),
                onValueChange = { juiceName = it },
                label = { Text("Please enter juice name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = juiceImage,
                isError = false,
                onValueChange = { juiceImage = it },
                label = { Text("Please enter text for juice image") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = if (juiceAvailability) "Juice is Available" else "Juice is not Available")
            Spacer(modifier = Modifier.height(5.dp))
            Switch(
                checked = juiceAvailability,
                onCheckedChange = { juiceAvailability = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    uncheckedThumbColor = MaterialTheme.colors.onSurface
                )
            )
            Row(horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        val drink = Drink(
                            drinkId = UUID.randomUUID().toString(),
                            drinkName = juiceName,
                            drinkImage = juiceImage,
                            isAvailable = juiceAvailability
                        )
                        juiceVendorViewModel.addNewDrink(drink = drink)
                        juiceVendorViewModel.updateAddJuiceComposableVisibility(status = false)
                    }
                }, enabled = juiceName.isNotEmpty(), modifier = Modifier.fillMaxWidth(0.3f)) {
                    Text("Save")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.align(Alignment.Start)) {
            Text(
                "Juices", style = MaterialTheme.typography.h4,
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
        UpdateJuicesComposable(juiceVendorViewModel, coroutineScope)
    }
}

@Composable
fun UpdateJuicesComposable(
    juiceVendorViewModel: JuiceVendorViewModel,
    coroutineScope: CoroutineScope
) {
    LaunchedEffect(juiceVendorViewModel) {
        coroutineScope.launch {
            juiceVendorViewModel.getDrinksList()
        }
    }

    val drinksUiState = juiceVendorViewModel.drinksUiState.collectAsState()
    when (drinksUiState.value) {
        is DrinksUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(10.dp))
                Text("Please wait while we fetch the juices list for you")
            }
        }

        is DrinksUiState.Success -> {
            JuicesListComposable(
                drinks = (drinksUiState.value as DrinksUiState.Success).drinks,
                juiceVendorViewModel
            )
        }

        is DrinksUiState.Error -> {
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
                    text = "Something went wrong while fetching the data, Please check your internet connection, if its fine please contact Admin",
                    modifier = Modifier.padding(30.dp),
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun JuicesListComposable(drinks: List<Drink>, juiceVendorViewModel: JuiceVendorViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(drinks.size) { index ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = drinks[index].drinkName)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = drinks[index].isAvailable,
                    onCheckedChange = { availability ->
                        juiceVendorViewModel.onJuiceAvailabilityUpdated(
                            availability = availability,
                            drinkId = drinks[index].drinkId
                        )
                    })
            }
        }
    }
}

