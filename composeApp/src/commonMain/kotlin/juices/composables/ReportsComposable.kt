package juices.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import common.composables.DateRangePickerComposable
import common.composables.ErrorComposable
import common.composables.LoadingComposable
import common.extensions.removeSpacesAndLowerCase
import data.Config
import juices.viewModels.ExportReportUiState
import juices.viewModels.GenerateReportUiState
import juices.viewModels.JuiceVendorViewModel
import juicevendor.composeapp.generated.resources.Res
import juicevendor.composeapp.generated.resources.ic_apple
import juicevendor.composeapp.generated.resources.ic_banana
import juicevendor.composeapp.generated.resources.ic_close
import juicevendor.composeapp.generated.resources.ic_coffee
import juicevendor.composeapp.generated.resources.ic_export
import juicevendor.composeapp.generated.resources.ic_fruit_bowl
import juicevendor.composeapp.generated.resources.ic_generic_juice
import juicevendor.composeapp.generated.resources.ic_lemon
import juicevendor.composeapp.generated.resources.ic_orange
import juicevendor.composeapp.generated.resources.ic_tea
import juicevendor.composeapp.generated.resources.ic_watermelon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ReportsComposable(juiceVendorViewModel: JuiceVendorViewModel) {
    val coroutineScope = rememberCoroutineScope()
    // Date picker variables
    val formatter = SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault())
    var startDate by remember { mutableStateOf(formatter.format(Date())) }
    var endDate by remember { mutableStateOf(formatter.format(Date())) }
    var totalOrderCount by remember { mutableStateOf(0) }
    val dateRangePickerState = rememberDateRangePickerState()
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val generateReportUiState = juiceVendorViewModel.generateReportUiState.collectAsState()
    val exportReportUiState = juiceVendorViewModel.exportReportUiState.collectAsState()

    LaunchedEffect(Unit) {
        juiceVendorViewModel.getReportForDateInterval(startDate = startDate, endDate = endDate)
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(Color.White)
            ) {
                DateRangePickerComposable(state = dateRangePickerState, showModeToggle = false) {
                    startDate = formatter.format(dateRangePickerState.selectedStartDateMillis)
                    endDate = formatter.format(dateRangePickerState.selectedEndDateMillis)
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        juiceVendorViewModel.getReportForDateInterval(
                            startDate = startDate,
                            endDate = endDate
                        )
                    }
                }
            }
        },
        content = {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).clickable {
                            juiceVendorViewModel.updateReportsComposableVisibility(status = false)
                        },
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                ReportHeader(
                    startDate = startDate,
                    endDate = endDate,
                    totalOrdersCount = totalOrderCount,
                    coroutineScope = coroutineScope,
                    bottomSheetState = bottomSheetState
                )
                Spacer(modifier = Modifier.height(10.dp))
                when (generateReportUiState.value) {
                    is GenerateReportUiState.Loading -> {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(20.dp)
                        ) {
                            LoadingComposable()
                        }
                    }

                    is GenerateReportUiState.Success -> {
                        val reportHashMap =
                            (generateReportUiState.value as GenerateReportUiState.Success).reportsHashMap
                        totalOrderCount =
                            (generateReportUiState.value as GenerateReportUiState.Success).totalOrderCount
                        if (reportHashMap.isEmpty()) {
                            Row(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                    .padding(20.dp)
                            ) {
                                Text("No Orders can be found for the given date range, Please try different date range")
                            }
                        }
                        if (reportHashMap.isNotEmpty()) {
                            Row(modifier = Modifier.align(Alignment.End)) {
                                Column {
                                    Row(
                                        modifier = Modifier.align(Alignment.End)
                                            .clickable {
                                                coroutineScope.launch {
                                                    juiceVendorViewModel.onReportExportButtonClicked(startDate = startDate, endDate = endDate)
                                                }
                                            }
                                            .padding(horizontal = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Export",
                                            textAlign = TextAlign.Center,
                                            fontSize = 15.sp
                                        )
                                        Image(
                                            painter = painterResource(Res.drawable.ic_export),
                                            contentDescription = "export",
                                            modifier = Modifier.size(15.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        "(works only on android for now)",
                                        textAlign = TextAlign.Center,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                            when (exportReportUiState.value) {
                                is ExportReportUiState.NoUserActionState -> {}
                                is ExportReportUiState.ExportInProgress -> {}
                                is ExportReportUiState.Success -> {
                                }

                                is ExportReportUiState.Error -> {}
                            }

                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            reportHashMap.forEach { map ->
                                item {
                                    RoundedCardView(
                                        juiceName = map.value.drinkName,
                                        orderCount = map.value.orderCount
                                    )
                                }
                            }
                        }
                    }

                    is GenerateReportUiState.Error -> {
                        ErrorComposable()
                    }
                }
            }
        },
        scrimColor = Color.Black.copy(alpha = 0.5f),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportHeader(
    startDate: String,
    endDate: String,
    totalOrdersCount: Int,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    val firstTextStyle = TextStyle(
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif
    )
    val restTextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.SansSerif
    )

    val orderText = buildAnnotatedString {
        append("$totalOrdersCount Orders for the date interval of ")
        pushStringAnnotation(tag = "startDate", annotation = "startDate")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colors.primary,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(startDate)
        }
        pop()
        append(" to ")
        pushStringAnnotation(tag = "endDate", annotation = "endDate")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colors.primary,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(endDate)
        }
        pop()
    }

    val annotatedString = buildAnnotatedString {
        withStyle(style = firstTextStyle.toSpanStyle()) {
            append(orderText.take(totalOrdersCount.toString().length))
        }
        withStyle(style = restTextStyle.toSpanStyle()) {
            append(orderText.drop(totalOrdersCount.toString().length))
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "startDate",
                start = offset,
                end = offset
            )
                .firstOrNull()?.let {
                    coroutineScope.launch { bottomSheetState.show() }
                }
            annotatedString.getStringAnnotations(
                tag = "endDate",
                start = offset,
                end = offset
            )
                .firstOrNull()?.let {
                    coroutineScope.launch { bottomSheetState.show() }
                }
        }
    )
}

fun getResourceDrawable(drinkName: String): DrawableResource {
    return when (drinkName.removeSpacesAndLowerCase()) {
        "orange" -> Res.drawable.ic_orange
        "apple" -> Res.drawable.ic_apple
        "banana" -> Res.drawable.ic_banana
        "lemon" -> Res.drawable.ic_lemon
        "watermelon" -> Res.drawable.ic_watermelon
        "fruitbowl" -> Res.drawable.ic_fruit_bowl
        "tea" -> Res.drawable.ic_tea
        "coffee" -> Res.drawable.ic_coffee
        else -> Res.drawable.ic_generic_juice
    }
}

@Composable
fun RoundedCardView(
    juiceName: String,
    orderCount: Int
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight(),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 10.dp).fillMaxWidth()) {

            Image(
                painter = painterResource(getResourceDrawable(juiceName)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = juiceName,
                style = MaterialTheme.typography.h4,
                color = Color.Black,
                fontSize = 18.sp
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = orderCount.toString(),
                style = MaterialTheme.typography.h2,
                color = Color.Black,
                fontSize = 30.sp
            )

        }
    }
}