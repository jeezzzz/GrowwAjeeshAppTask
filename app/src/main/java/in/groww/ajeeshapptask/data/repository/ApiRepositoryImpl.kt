package `in`.groww.ajeeshapptask.data.repository

import android.util.Log
import `in`.groww.ajeeshapptask.domain.mapper.toDomain
import `in`.groww.ajeeshapptask.data.remote.service.ApiService
import `in`.groww.ajeeshapptask.domain.model.companyOverview.CompanyOverviewResponse
import `in`.groww.ajeeshapptask.domain.model.searchResult.SearchResult
import `in`.groww.ajeeshapptask.domain.model.timeSeries.TimeSeries
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.Stock
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.TopGainersTopLosersResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ApiRepository {
    private var cachedResponse: TopGainersTopLosersResponse? = null
    private var lastUpdated: Long = 0
    private val cacheDuration = 30 * 60 * 1000

    override suspend fun getTopGainersLosers(): TopGainersTopLosersResponse {
        return if (shouldRefreshCache()) {
            api.getTopGainersLosers().toDomain().also {
                Log.d("API", "Raw response: ${it.toString()}")
                cachedResponse = it
                lastUpdated = System.currentTimeMillis()
            }
        } else {
            cachedResponse ?: throw IllegalStateException("Cache not initialized")
        }
    }

    override fun getTopGainers(): List<Stock> {
        return cachedResponse?.topGainers?.sortedByDescending { it.changePercentage } ?: emptyList()
    }

    override fun getTopLosers(): List<Stock> {
        return cachedResponse?.topLosers?.sortedBy { it.changePercentage } ?: emptyList()
    }

    override fun getMAT(): List<Stock> {
        return cachedResponse?.mostActivelyTraded?.sortedBy { it.changePercentage } ?: emptyList()
    }

    private fun shouldRefreshCache(): Boolean {
        return cachedResponse == null || (System.currentTimeMillis() - lastUpdated) > cacheDuration
    }

    suspend fun clearCache() {
        cachedResponse = null
        lastUpdated = 0
    }
}

@Singleton
class StockDetailsRepository @Inject constructor(
    private val api: ApiService
) {
    private val overviewCache = mutableMapOf<String, CompanyOverviewResponse>()
    private val timeSeriesCache = mutableMapOf<String, TimeSeries>()

    suspend fun getStockOverview(symbol: String): CompanyOverviewResponse {
        return overviewCache.getOrPut(symbol) {
            api.getCompanyOverview(symbol=symbol)
                .toDomain()
        }
    }

    suspend fun getStockDailyData(symbol: String): TimeSeries {
        return timeSeriesCache.getOrPut(symbol) {
            api.getDailyTimeSeries(symbol=symbol)
                .toDomain()
        }
    }
}

class StockSearchRepository @Inject constructor(
    private val api: ApiService
) {
    private val cache = mutableMapOf<String, List<SearchResult>>()

    suspend fun searchSymbols(query: String): List<SearchResult> {
        if (query.length < 3) return emptyList()

        return cache.getOrPut(query) {
            api.symbolSearch(keywords = query).body()?.bestMatches?.mapNotNull { dto ->
                try {
                    SearchResult(
                        symbol = dto.symbol,
                        name = dto.name,
                        type = dto.type,
                        region = dto.region,
                        currency = dto.currency,
                        matchScore = dto.matchScore.toFloat()
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
        }
    }
}
