package `in`.groww.ajeeshapptask.domain.model.timeSeries

data class TimeSeries(
    val metaData: MetaData,
    val timeSeriesDaily: List<DailyData>
)

data class MetaData(
    val symbol: String,
    val lastRefreshed: String,
    val timeZone: String
)

data class DailyData(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)
