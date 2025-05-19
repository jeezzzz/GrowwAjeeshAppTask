package `in`.groww.ajeeshapptask.domain.model.companyOverview

//data class CompanyOverviewResponse(
//    val week52High: Double,
//    val week52Low: Double,
//    val day200MovingAvg: Double,
//    val day50MovingAvg: Double,
//    val marketCap: Long,
//    val name: String,
//    val description: String,
//    val exchange: String,
//    val currency: String,
//    val sector: String,
//    val industry: String,
//    val peRatio: Double,
//    val dividendYield: Double,
//    val profitMargin: Double,
//    val beta: Double
//)

data class CompanyOverviewResponse(
    val week52High: Double = 0.0,
    val week52Low: Double = 0.0,
    val marketCap: Long = 0L,
    val name: String = "",
    val description: String = "",
    val exchange: String = "",
    val currency: String = "",
    val sector: String = "",
    val industry: String = "",
    val peRatio: Double = 0.0,
    val dividendYield: Double = 0.0
)

