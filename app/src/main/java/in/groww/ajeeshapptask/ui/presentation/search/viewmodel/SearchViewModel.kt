package `in`.groww.ajeeshapptask.ui.presentation.search.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.groww.ajeeshapptask.data.local.RecentSearchManager
import `in`.groww.ajeeshapptask.data.repository.StockSearchRepository
import `in`.groww.ajeeshapptask.domain.model.utils.RecentSearch
import `in`.groww.ajeeshapptask.domain.model.searchResult.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StockSearchRepository,
    private val recentSearchManager: RecentSearchManager
) : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.IDLE)

    val searchResults: StateFlow<List<SearchResult>> = _searchResults
    val loadingState: StateFlow<LoadingState> = _loadingState
    val recentSearches: StateFlow<List<RecentSearch>> =
        recentSearchManager.getRecentSearches()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            snapshotFlow { searchQuery }
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collectLatest { query ->
                    try {
                        _loadingState.value = LoadingState.LOADING
                        _searchResults.value = repository.searchSymbols(query)
                        _loadingState.value = LoadingState.SUCCESS
                    } catch (e: Exception) {
                        _loadingState.value = LoadingState.ERROR(e.message ?: "Unknown error")
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
    }

    fun onSymbolSelected(symbol: String, name: String) {
        viewModelScope.launch {
            recentSearchManager.addRecentSearch(RecentSearch(symbol, name))
        }
    }
}

sealed class LoadingState {
    object IDLE : LoadingState()
    object LOADING : LoadingState()
    data class ERROR(val message: String) : LoadingState()
    object SUCCESS : LoadingState()
}
