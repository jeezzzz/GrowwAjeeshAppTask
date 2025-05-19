package `in`.groww.ajeeshapptask.domain.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
//data class CompanyOverviewDto(
//    @Json(name = "52WeekHigh") val week52High: String,
//    @Json(name = "52WeekLow") val week52Low: String,
//    @Json(name = "200DayMovingAverage") val day200MovingAvg: String,
//    @Json(name = "50DayMovingAverage") val day50MovingAvg: String,
//    @Json(name = "MarketCapitalization") val marketCap: String,
//    @Json(name = "Name") val name: String,
//    @Json(name = "Description") val description: String,
//    @Json(name = "Exchange") val exchange: String,
//    @Json(name = "Currency") val currency: String,
//    @Json(name = "Sector") val sector: String,
//    @Json(name = "Industry") val industry: String,
//    @Json(name = "PERatio") val peRatio: String,
//    @Json(name = "DividendYield") val dividendYield: String,
//    @Json(name = "ProfitMargin") val profitMargin: String,
//    @Json(name = "Beta") val beta: String
//)

@JsonClass(generateAdapter = true)
data class CompanyOverviewDto(
    @Json(name = "52WeekHigh") val week52High: String? = "",
    @Json(name = "52WeekLow") val week52Low: String? = "",
    @Json(name = "MarketCapitalization") val marketCap: String? = "",
    @Json(name = "Name") val name: String? = "",
    @Json(name = "Description") val description: String? = "",
    @Json(name = "Exchange") val exchange: String? = "",
    @Json(name = "Currency") val currency: String? = "",
    @Json(name = "Sector") val sector: String? = "",
    @Json(name = "Industry") val industry: String? = "",
    @Json(name = "PERatio") val peRatio: String? = "",
    @Json(name = "DividendYield") val dividendYield: String? = ""
)

