package composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import data.Drink
import kotlinx.coroutines.launch
import viewModels.JuiceVendorViewModel
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNewJuiceComposable(juiceVendorViewModel: JuiceVendorViewModel) {
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    var juiceName by remember { mutableStateOf("") }
    var juiceImage by remember { mutableStateOf("") }
    var juiceAvailability by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        }, enabled = juiceName.isNotEmpty(), modifier = Modifier.fillMaxWidth(0.2f)) {
            Text("Save")
        }
    }
}