package `in`.groww.ajeeshapptask.ui.presentation.explore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.groww.ajeeshapptask.data.local.RecentSearchManager
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.TopGainersTopLosersResponse
import `in`.groww.ajeeshapptask.data.repository.ApiRepository
import `in`.groww.ajeeshapptask.ui.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: ApiRepository,
    recentSearchManager: RecentSearchManager
) : ViewModel() {

    val recentSearches = recentSearchManager.getRecentSearches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _state = MutableStateFlow<Resource<TopGainersTopLosersResponse>>(Resource.Loading)
    val state = _state.asStateFlow()

    init {
        fetchTopGainersLosers()
    }

    fun fetchTopGainersLosers() {
        viewModelScope.launch {
            _state.value = Resource.Loading
            try {
                val response = repository.getTopGainersLosers()
                _state.value = Resource.Success(response)
                Log.d("check", response.toString())
            } catch (e: Exception) {
                _state.value = Resource.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

