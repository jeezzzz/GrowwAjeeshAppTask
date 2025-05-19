package `in`.groww.ajeeshapptask.ui.presentation.detail.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.groww.ajeeshapptask.data.repository.StockDetailsRepository
import `in`.groww.ajeeshapptask.domain.model.utils.ChartEntry
import `in`.groww.ajeeshapptask.domain.model.companyOverview.CompanyOverviewResponse
import `in`.groww.ajeeshapptask.ui.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val repository: StockDetailsRepository
) : ViewModel() {
    private val _state = MutableStateFlow<Resource<StockDetailState>>(Resource.Loading)
    val state = _state.asStateFlow()

    private var allChartData = emptyList<ChartEntry>()
    private lateinit var overview: CompanyOverviewResponse

    fun loadStockDetails(symbol: String) {
        viewModelScope.launch {
            _state.value = Resource.Loading
            try {
                overview = repository.getStockOverview(symbol)
                val timeSeries = repository.getStockDailyData(symbol)

                allChartData = timeSeries.timeSeriesDaily.map { dailyData ->
                    ChartEntry(
                        date = dailyData.date,
                        close = dailyData.close.toFloat()
                    )
                }.sortedBy { it.date }

                val (currentPrice, priceChange) = calculatePriceMetrics(allChartData)

                _state.value = Resource.Success(
                    StockDetailState(
                        overview = overview,
                        chartData = allChartData,
                        currentPrice = currentPrice,
                        priceChange = priceChange,
                        filteredChartData = allChartData
                    )
                )

            } catch (e: Exception) {
                _state.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterChartData(period: String) {
        val currentData = allChartData.toList()
        val filtered = when (period) {
            "1D" -> currentData.takeLast(1)
            "1W" -> currentData.filter { isWithinDays(it.date, 7) }
            "1M" -> currentData.filter { isWithinDays(it.date, 30) }
            "6M" -> currentData.filter { isWithinDays(it.date, 180) }
            "1Y" -> currentData.filter { isWithinDays(it.date, 365) }
            else -> currentData
        }

        val (currentPrice, priceChange) = calculatePriceMetrics(filtered)

        val currentState = when (val s = _state.value) {
            is Resource.Success -> s.data
            else -> null
        }

        _state.value = Resource.Success(
            currentState?.copy(
                filteredChartData = filtered,
                currentPrice = currentPrice,
                priceChange = priceChange
            ) ?: StockDetailState(
                overview = overview,
                chartData = filtered,
                currentPrice = currentPrice,
                priceChange = priceChange,
                filteredChartData = filtered
            )
        )
    }

    private fun calculatePriceMetrics(data: List<ChartEntry>): Pair<Double, Double> {
        if (data.isEmpty()) return 0.0 to 0.0

        val latestClose = data.last().close.toDouble()
        val previousClose = data.getOrNull(data.lastIndex - 1)?.close?.toDouble() ?: latestClose
        val priceChange = ((latestClose - previousClose) / previousClose) * 100

        return latestClose to priceChange
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isWithinDays(date: String, days: Int): Boolean {
        return try {
            val entryDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
            val cutoffDate = LocalDate.now().minusDays(days.toLong())
            entryDate.isAfter(cutoffDate)
        } catch (_: Exception) {
            false
        }
    }
}

data class StockDetailState(
    val overview: CompanyOverviewResponse,
    val chartData: List<ChartEntry>,
    val currentPrice: Double,
    val priceChange: Double,
    val filteredChartData: List<ChartEntry>
)
