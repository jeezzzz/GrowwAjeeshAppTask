package `in`.groww.ajeeshapptask.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.groww.ajeeshapptask.domain.model.utils.RecentSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get


private val Context.dataStore by preferencesDataStore("recent_searches")

@Singleton
class RecentSearchManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val RECENT_SEARCHES_KEY = stringSetPreferencesKey("recent_searches_set")

    suspend fun addRecentSearch(search: RecentSearch) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[RECENT_SEARCHES_KEY] ?: emptySet()
            val newSet = currentSet.toMutableSet().apply {
                removeIf { it.startsWith("${search.symbol}|") }
                add(serializeSearch(search))
                if (size > 10) remove(first())
            }
            preferences[RECENT_SEARCHES_KEY] = newSet
        }
    }

    fun getRecentSearches(): Flow<List<RecentSearch>> = context.dataStore.data
        .map { preferences ->
            preferences[RECENT_SEARCHES_KEY]?.mapNotNull { deserializeSearch(it) }
                ?.sortedByDescending { it.timestamp } ?: emptyList()
        }

    private fun serializeSearch(search: RecentSearch): String {
        return "${search.symbol}|${search.name}|${search.timestamp}"
    }

    private fun deserializeSearch(string: String): RecentSearch? {
        return try {
            val parts = string.split("|")
            RecentSearch(
                symbol = parts[0],
                name = parts[1],
                timestamp = parts[2].toLong()
            )
        } catch (e: Exception) {
            null
        }
    }
}
