package `in`.groww.ajeeshapptask.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore by preferencesDataStore("theme_prefs")

@Singleton
class ThemePreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val USER_SET_KEY = booleanPreferencesKey("user_set")
    }

    val themeFlow: Flow<Pair<Boolean?, Boolean>> = context.themeDataStore.data.map { prefs ->
        Pair(prefs[DARK_MODE_KEY], prefs[USER_SET_KEY] == true)
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
            prefs[USER_SET_KEY] = true
        }
    }

    suspend fun clearUserChoice() {
        context.themeDataStore.edit { prefs ->
            prefs.remove(DARK_MODE_KEY)
            prefs[USER_SET_KEY] = false
        }
    }
}
