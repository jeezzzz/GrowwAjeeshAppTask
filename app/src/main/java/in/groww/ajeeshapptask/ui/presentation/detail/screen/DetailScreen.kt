package `in`.groww.ajeeshapptask.ui.presentation.detail.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.groww.ajeeshapptask.domain.model.utils.ChartEntry
import `in`.groww.ajeeshapptask.ui.presentation.detail.viewmodel.StockDetailState
import `in`.groww.ajeeshapptask.ui.presentation.detail.viewmodel.StockDetailViewModel
import `in`.groww.ajeeshapptask.ui.utils.ErrorState
import `in`.groww.ajeeshapptask.ui.utils.FullScreenLoader
import `in`.groww.ajeeshapptask.ui.utils.Resource
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: StockDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedPeriod by remember { mutableStateOf("6M") }

    LaunchedEffect(symbol) {
        viewModel.loadStockDetails(symbol)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state) {
            is Resource.Loading -> FullScreenLoader()
            is Resource.Error -> ErrorState((state as Resource.Error).message)
            is Resource.Success -> {
                val data = (state as Resource.Success<StockDetailState>).data
                DetailContent(
                    data = data,
                    selectedPeriod = selectedPeriod,
                    onBack = onBack,
                    onPeriodSelected = {
                        selectedPeriod = it
                        viewModel.filterChartData(it)
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailContent(
    data: StockDetailState,
    selectedPeriod: String,
    onBack: () -> Unit,
    onPeriodSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp) // extra space at bottom
    ) {
        // Top App Bar with status bar inset
        Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 10.dp, horizontal = 20.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Stock Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // Company Info
        Column(Modifier.padding(horizontal = 10.dp, vertical = 16.dp)) {
            Text(
                text = "${data.overview.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "$${"%.2f".format(data.currentPrice)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = String.format("%.2f%%", data.priceChange),
                color = if (data.priceChange >= 0) Color(0xFF00C853) else Color.Red,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Exchange: ${data.overview.exchange.orEmpty()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        // Time filter buttons and chart
        if (data.filteredChartData.isNotEmpty()) {
            TimeFilterButtons(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected
            )
            StockChart(
                chartData = data.filteredChartData,
                priceChange = data.priceChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        // About section
        if (!data.overview.description.isNullOrBlank()) {
            Text(
                text = "About ${data.overview.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = data.overview.description,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        // Industry and sector chips in a column with spacing
        if (!data.overview.industry.isNullOrBlank() || !data.overview.sector.isNullOrBlank()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!data.overview.industry.isNullOrBlank()) {
                    CategoryChip(text = "Industry: ${data.overview.industry}")
                }
                if (!data.overview.sector.isNullOrBlank()) {
                    CategoryChip(text = "Sector: ${data.overview.sector}")
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // 52-week range
        if ((data.overview.week52High ?: 0.0) > 0 && (data.overview.week52Low ?: 0.0) > 0) {
            Text(
                text = "52-Week Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            PriceRangeIndicator(
                low = data.overview.week52Low ?: 0.0,
                high = data.overview.week52High ?: 0.0,
                current = data.currentPrice,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        // Key statistics
        val metrics = listOfNotNull(
            data.overview.marketCap?.takeIf { it > 0 }?.let { "Market Cap" to "$${formatLargeNumber(it)}" },
            data.overview.peRatio?.takeIf { it > 0 }?.let { "P/E Ratio" to "%.2f".format(it) },
            data.overview.dividendYield?.takeIf { it > 0 }?.let { "Dividend Yield" to "%.2f%%".format(it) }
        )
        if (metrics.isNotEmpty()) {
            Text(
                text = "Key Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            StatsGrid(metrics)
            Spacer(Modifier.height(24.dp))
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChart(
    chartData: List<ChartEntry>,
    priceChange: Double,
    modifier: Modifier = Modifier
) {
    val color = if (priceChange >= 0) Color(0xFF00C853) else Color.Red
    var selectedPoint by remember { mutableStateOf<ChartEntry?>(null) }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .pointerInput(chartData) {
                detectTapGestures { tapOffset ->
                    val x = tapOffset.x
                    val itemWidth = size.width / (chartData.size - 1).coerceAtLeast(1)
                    val index = (x / itemWidth).toInt().coerceIn(0, chartData.lastIndex)
                    selectedPoint = chartData.getOrNull(index)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (chartData.size < 2) return@Canvas

            val priceValues = chartData.map { it.close }
            val maxPrice = priceValues.maxOrNull() ?: 0f
            val minPrice = priceValues.minOrNull() ?: 0f
            val priceRange = (maxPrice - minPrice).takeIf { it != 0f } ?: 1f

            val xStep = size.width / (chartData.size - 1).coerceAtLeast(1)
            val yRatio = size.height / priceRange

            // Draw line chart
            val path = Path().apply {
                chartData.forEachIndexed { index, entry ->
                    val x = xStep * index
                    val y = size.height - (entry.close - minPrice) * yRatio
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw selected point
            selectedPoint?.let { point ->
                val index = chartData.indexOf(point)
                val x = xStep * index
                val y = size.height - (point.close - minPrice) * yRatio
                drawCircle(
                    color = color,
                    radius = 8f,
                    center = Offset(x, y)
                )
            }
        }

        selectedPoint?.let { point ->
            val date = try {
                LocalDate.parse(point.date).format(DateTimeFormatter.ofPattern("MMM dd"))
            } catch (e: Exception) {
                point.date
            }
            Text(
                text = "$${"%.2f".format(point.close)} - $date",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TimeFilterButtons(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val periods = listOf("1D", "1W", "1M", "6M", "1Y")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        periods.forEach { period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { onPeriodSelected(period) },
                label = { Text(period) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(text: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PriceRangeIndicator(
    low: Double,
    high: Double,
    current: Double,
    modifier: Modifier = Modifier
) {
    if (high <= low || high == 0.0) return // Hide if invalid

    val percentage = ((current - low) / (high - low)).coerceIn(0.0, 1.0)
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.LightGray, MaterialTheme.shapes.small)
        )
        Box(
            modifier = Modifier
                .offset(
                    x = (
                            (percentage.toFloat() *
                                    (LocalConfiguration.current.screenWidthDp.dp - 64.dp).value)
                                .coerceAtLeast(0f)
                            ).dp
                )
                .size(16.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$${"%.2f".format(low)}")
        Text(text = "$${"%.2f".format(high)}")
    }
}

@Composable
fun StatsGrid(metrics: List<Pair<String, String>>) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        metrics.chunked(2).forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { (name, value) ->
                    StatItem(name, value)
                }
            }
        }
    }
}

@Composable
fun StatItem(name: String, value: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000_000 -> String.format("%.2fB", number / 1_000_000_000.0)
        number >= 1_000_000 -> String.format("%.2fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.2fK", number / 1_000.0)
        else -> number.toString()
    }
}
