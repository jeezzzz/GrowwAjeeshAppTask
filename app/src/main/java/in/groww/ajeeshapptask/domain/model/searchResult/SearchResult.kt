package `in`.groww.ajeeshapptask.domain.model.searchResult

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// SearchResult.kt
data class SearchResult(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val currency: String,
    val matchScore: Float
)

// API Response DTO
@JsonClass(generateAdapter = true)
data class SymbolSearchResponse(
    @Json(name = "bestMatches") val bestMatches: List<BestMatchDto>
)

@JsonClass(generateAdapter = true)
data class BestMatchDto(
    @Json(name = "1. symbol") val symbol: String,
    @Json(name = "2. name") val name: String,
    @Json(name = "3. type") val type: String,
    @Json(name = "4. region") val region: String,
    @Json(name = "8. currency") val currency: String,
    @Json(name = "9. matchScore") val matchScore: String
)
