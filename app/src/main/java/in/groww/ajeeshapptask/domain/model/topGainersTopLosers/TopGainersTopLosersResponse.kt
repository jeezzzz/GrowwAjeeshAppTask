package `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers

data class TopGainersTopLosersResponse(
    val lastUpdated: String,
    val metadata: String,
    val mostActivelyTraded: List<Stock>,
    val topGainers: List<Stock>,
    val topLosers: List<Stock>
)

data class Stock(
    val ticker: String,
    val price: String,
    val changeAmount: String,
    val changePercentage: String,
    val volume: String
)

//data class TopGainer(
//    val ticker: String,
//    val price: String,
//    val changeAmount: String,
//    val changePercentage: String,
//    val volume: String
//)
//
//data class TopLoser(
//    val ticker: String,
//    val price: String,
//    val changeAmount: String,
//    val changePercentage: String,
//    val volume: String
//)
