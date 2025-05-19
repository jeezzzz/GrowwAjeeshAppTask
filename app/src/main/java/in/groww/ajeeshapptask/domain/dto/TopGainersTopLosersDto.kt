package `in`.groww.ajeeshapptask.domain.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopGainersTopLosersResponseDto(
    @Json(name = "Information") val information: String?,
    @Json(name = "Note") val note: String?,
    @Json(name = "last_updated") val lastUpdated: String?,
    @Json(name = "metadata") val metadata: String?,
    @Json(name = "most_actively_traded") val mostActivelyTraded: List<StockDto>?,
    @Json(name = "top_gainers") val topGainers: List<StockDto>?,
    @Json(name = "top_losers") val topLosers: List<StockDto>?
)

@JsonClass(generateAdapter = true)
data class StockDto(
    @Json(name = "ticker") val ticker: String?,
    @Json(name = "price") val price: String?,
    @Json(name = "change_amount") val changeAmount: String?,
    @Json(name = "change_percentage") val changePercentage: String?,
    @Json(name = "volume") val volume: String?
)
