package `in`.groww.ajeeshapptask.ui.presentation.listing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.groww.ajeeshapptask.data.repository.ApiRepository
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.Stock
import `in`.groww.ajeeshapptask.ui.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {
    private val _state = MutableStateFlow<Resource<List<Stock>>>(Resource.Loading)
    val state = _state.asStateFlow()

    private var allStocks = emptyList<Stock>()
    private var currentPage = 1
    private val pageSize = 10 // Items per page

    fun fetchStocks(category: String) {
        viewModelScope.launch {
            _state.value = Resource.Loading
            try {
                // Fetch fresh data each time
                val response = repository.getTopGainersLosers()
                allStocks = when (category) {
                    "gainers" -> response.topGainers
                    "losers" -> response.topLosers
                    "mat" -> response.mostActivelyTraded
                    else -> emptyList()
                }
                loadPage()
            } catch (e: Exception) {
                _state.value = Resource.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loadNextPage() {
        if (hasMoreItems()) {
            currentPage++
            loadPage()
        }
    }

    private fun loadPage() {
        val start = (currentPage - 1) * pageSize
        val end = minOf(start + pageSize, allStocks.size)
        val pagedStocks = allStocks.subList(0, end)

        _state.value = Resource.Success(pagedStocks)
    }

    internal fun hasMoreItems() = allStocks.size > currentPage * pageSize
}
