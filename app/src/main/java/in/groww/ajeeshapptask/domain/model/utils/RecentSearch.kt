package `in`.groww.ajeeshapptask.domain.model.utils

data class RecentSearch(
    val symbol: String,
    val name: String,
    val timestamp: Long = System.currentTimeMillis()
)