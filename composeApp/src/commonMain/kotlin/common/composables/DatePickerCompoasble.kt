package common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar

fun getFormattedDate(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat(data.Config.DATE_FORMAT)
    return dateFormat.format(calender.timeInMillis)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangePickerComposable(
    state: DateRangePickerState,
    showModeToggle: Boolean,
    onDoneButtonClicked: () -> Unit
) {
    DateRangePicker(
        state,
        modifier = Modifier,
        title = {
            Text(
                text = "Select date range", modifier = Modifier
                    .padding(16.dp)
            )
        },
        headline = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    (if (state.selectedStartDateMillis != null) state.selectedStartDateMillis?.let {
                        getFormattedDate(
                            it
                        )
                    } else "Start Date")?.let { Text(text = it) }
                }
                Box(Modifier.weight(1f)) {
                    (if (state.selectedEndDateMillis != null) state.selectedEndDateMillis?.let {
                        getFormattedDate(
                            it
                        )
                    } else "End Date")?.let { Text(text = it) }
                }
                Box(Modifier.weight(0.2f)) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Ok",
                        modifier = Modifier.clickable {
                            onDoneButtonClicked.invoke()
                        })
                }
            }
        },
        showModeToggle = showModeToggle,
    )
}