package `in`.groww.ajeeshapptask.ui.presentation.listing.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.Stock
import `in`.groww.ajeeshapptask.ui.components.StockCard
import `in`.groww.ajeeshapptask.ui.presentation.listing.viewmodel.ListingViewModel
import `in`.groww.ajeeshapptask.ui.utils.ErrorState
import `in`.groww.ajeeshapptask.ui.utils.FullScreenLoader
import `in`.groww.ajeeshapptask.ui.utils.Resource

@Composable
fun ListingScreen(category: String, onStockClick: (String) -> Unit, onBack: () -> Unit,) {
    val viewModel: ListingViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState().value
    val lazyGridState = rememberLazyGridState()

    val heading = when(category){
        "gainers" -> "Top Gainers"
        "losers" -> "Top Loser"
        "mat" -> "Most Actively Traded"
        else -> " "
    }

    LaunchedEffect(category) {
        viewModel.fetchStocks(category)
    }

    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() &&
                    visibleItems.last().index >= lazyGridState.layoutInfo.totalItemsCount - 2
                ) {
                    viewModel.loadNextPage()
                }
            }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ,
        color = MaterialTheme.colorScheme.background
    ) {
        when (state) {
            is Resource.Loading -> FullScreenLoader()
            is Resource.Error -> ErrorState(message = state.message)
            is Resource.Success<*> -> {
                val stocks = (state as Resource.Success<List<Stock>>).data

                ListingContent(heading, lazyGridState, stocks, onStockClick, viewModel, onBack)

                }
            }
        }
    }

@Composable
fun ListingContent(
    heading: String,
    lazyGridState: LazyGridState,
    stocks: List<Stock>,
    onStockClick: (String) -> Unit,
    viewModel: ListingViewModel,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)) {
        // Fixed, non-scrollable header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp)
                .background(MaterialTheme.colorScheme.background)
//                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = heading,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        // The grid fills the rest of the screen and is scrollable
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = lazyGridState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f) // <-- This is the key!
        ) {
            itemsIndexed(stocks) { index, stock ->
                StockCard(stock = stock, onClick = { onStockClick(stock.toString()) })
            }
            item(span = { GridItemSpan(2) }) {
                if (viewModel.hasMoreItems()) {
                    LoadingIndicator()
                } else {
                    EndOfList()
                }
            }
        }
    }
}



@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EndOfList() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No more items to load", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
    }
}
