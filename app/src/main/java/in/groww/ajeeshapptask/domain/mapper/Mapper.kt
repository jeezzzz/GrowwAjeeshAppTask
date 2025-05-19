package `in`.groww.ajeeshapptask.domain.mapper

import `in`.groww.ajeeshapptask.domain.dto.CompanyOverviewDto
import `in`.groww.ajeeshapptask.domain.dto.DailyDataDto
import `in`.groww.ajeeshapptask.domain.dto.MetaDataDto
import `in`.groww.ajeeshapptask.domain.dto.StockDto
import `in`.groww.ajeeshapptask.domain.dto.TimeSeriesDto
import `in`.groww.ajeeshapptask.domain.dto.TopGainersTopLosersResponseDto
import `in`.groww.ajeeshapptask.domain.model.companyOverview.CompanyOverviewResponse
import `in`.groww.ajeeshapptask.domain.model.timeSeries.DailyData
import `in`.groww.ajeeshapptask.domain.model.timeSeries.MetaData
import `in`.groww.ajeeshapptask.domain.model.timeSeries.TimeSeries
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.Stock
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.TopGainersTopLosersResponse

fun StockDto.toDomain() = Stock(
    ticker = ticker.orEmpty(),
    price = price.orEmpty(),
    changeAmount = changeAmount.orEmpty(),
    changePercentage = changePercentage.orEmpty(),
    volume = volume.orEmpty()
)

fun TopGainersTopLosersResponseDto.toDomain(): TopGainersTopLosersResponse {
    return TopGainersTopLosersResponse(
        lastUpdated = lastUpdated.orEmpty(),
        metadata = metadata.orEmpty(),
        mostActivelyTraded = mostActivelyTraded?.map { it.toDomain() } ?: emptyList(),
        topGainers = topGainers?.map { it.toDomain() } ?: emptyList(),
        topLosers = topLosers?.map { it.toDomain() } ?: emptyList()
    )
}

//fun CompanyOverviewDto.toDomain() = CompanyOverviewResponse(
//    week52High = week52High.toDoubleOrNull() ?: 0.0,
//    week52Low = week52Low.toDoubleOrNull() ?: 0.0,
//    day200MovingAvg = day200MovingAvg.toDoubleOrNull() ?: 0.0,
//    day50MovingAvg = day50MovingAvg.toDoubleOrNull() ?: 0.0,
//    marketCap = marketCap.toLongOrNull() ?: 0L,
//    name = name,
//    description = description,
//    exchange = exchange,
//    currency = currency,
//    sector = sector,
//    industry = industry,
//    peRatio = peRatio.toDoubleOrNull() ?: 0.0,
//    dividendYield = dividendYield.toDoubleOrNull() ?: 0.0,
//    profitMargin = profitMargin.toDoubleOrNull() ?: 0.0,
//    beta = beta.toDoubleOrNull() ?: 0.0
//)

fun CompanyOverviewDto.toDomain() = CompanyOverviewResponse(
    week52High = week52High?.toDoubleOrNull() ?: 0.0,
    week52Low = week52Low?.toDoubleOrNull() ?: 0.0,
    marketCap = marketCap?.toLongOrNull() ?: 0L,
    name = name ?: "",
    description = description ?: "",
    exchange = exchange ?: "",
    currency = currency ?: "",
    sector = sector ?: "",
    industry = industry ?: "",
    peRatio = peRatio?.toDoubleOrNull() ?: 0.0,
    dividendYield = dividendYield?.toDoubleOrNull() ?: 0.0
)


// Time Series Mapper
fun TimeSeriesDto.toDomain() = TimeSeries(
    metaData = metaData.toDomain(),
    timeSeriesDaily = timeSeriesDaily.map { (date, data) ->
        data.toDomain(date)
    }.sortedBy { it.date }
)

fun MetaDataDto.toDomain() = MetaData(
    symbol = symbol,
    lastRefreshed = lastRefreshed,
    timeZone = timeZone
)

fun DailyDataDto.toDomain(date: String) = DailyData(
    date = date,
    open = open.toDoubleOrNull() ?: 0.0,
    high = high.toDoubleOrNull() ?: 0.0,
    low = low.toDoubleOrNull() ?: 0.0,
    close = close.toDoubleOrNull() ?: 0.0,
    volume = volume.toLongOrNull() ?: 0L
)
