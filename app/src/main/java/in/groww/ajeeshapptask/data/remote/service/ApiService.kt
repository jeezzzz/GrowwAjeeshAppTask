package `in`.groww.ajeeshapptask.data.remote.service

import `in`.groww.ajeeshapptask.BuildConfig
import `in`.groww.ajeeshapptask.domain.dto.CompanyOverviewDto
import `in`.groww.ajeeshapptask.domain.dto.TimeSeriesDto
import `in`.groww.ajeeshapptask.domain.dto.TopGainersTopLosersResponseDto
import `in`.groww.ajeeshapptask.domain.model.searchResult.SymbolSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("query")
    suspend fun getTopGainersLosers(
        @Query("function") function: String = "TOP_GAINERS_LOSERS",
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_API_KEY
    ): TopGainersTopLosersResponseDto

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_API_KEY
    ): CompanyOverviewDto

    @GET("query")
    suspend fun getDailyTimeSeries(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_API_KEY
    ): TimeSeriesDto

    @GET("query")
    suspend fun symbolSearch(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_API_KEY
    ): Response<SymbolSearchResponse>

}
