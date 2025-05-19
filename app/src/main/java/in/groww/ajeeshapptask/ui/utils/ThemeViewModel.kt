package `in`.groww.ajeeshapptask.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.groww.ajeeshapptask.data.local.ThemePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferenceManager: ThemePreferenceManager
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)

    val isDarkMode: StateFlow<Boolean?> = _isDarkMode

    init {
        viewModelScope.launch {
            themePreferenceManager.themeFlow.collect { (dark, userSet) ->
                _isDarkMode.value = if (userSet) dark else null
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { themePreferenceManager.setDarkMode(enabled) }
    }
}
