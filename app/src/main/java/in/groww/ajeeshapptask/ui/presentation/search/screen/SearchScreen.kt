package `in`.groww.ajeeshapptask.ui.presentation.search.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import `in`.groww.ajeeshapptask.domain.model.utils.RecentSearch
import `in`.groww.ajeeshapptask.domain.model.searchResult.SearchResult
import `in`.groww.ajeeshapptask.ui.presentation.search.viewmodel.LoadingState
import `in`.groww.ajeeshapptask.ui.presentation.search.viewmodel.SearchViewModel
import `in`.groww.ajeeshapptask.ui.utils.ErrorState
import `in`.groww.ajeeshapptask.ui.utils.FullScreenLoader

@Composable
fun SearchScreen(
    navController: NavHostController,
    onSymbolSelected: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by remember { derivedStateOf { viewModel.searchQuery } }
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())
    val loadingState by viewModel.loadingState.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState(initial = emptyList())

    var selectedCategory by remember { mutableStateOf("All") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onNavigateBack = { navController.popBackStack() }
            )

            CategoryFilters(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                color = MaterialTheme.colorScheme.background
            ) {
                when {
                    searchQuery.isEmpty() -> RecentSearchesList(
                        searches = recentSearches,
                        onSymbolSelected = onSymbolSelected
                    )

                    loadingState is LoadingState.LOADING -> FullScreenLoader()
                    loadingState is LoadingState.ERROR -> ErrorState(
                        message = (loadingState as LoadingState.ERROR).message
                    )

                    else -> {
                        val filteredResults =
                            filterResultsByCategory(searchResults, selectedCategory)
                        SearchResultsList(
                            results = filteredResults,
                            onSymbolSelected = { symbol ->
                                val name = searchResults.find { it.symbol == symbol }?.name ?: ""
                                viewModel.onSymbolSelected(symbol, name)
                                navController.navigate("detail/$symbol")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .height(55.dp)
            .fillMaxWidth(),
        placeholder = { Text("Search symbols...") },
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.background
        )
    )
}


@Composable
fun CategoryFilters(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Stocks", "F&O", "MF", "ETF")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(color = MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onCategorySelected(category) }
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

fun filterResultsByCategory(
    results: List<SearchResult>,
    category: String
): List<SearchResult> {
    return when (category) {
        "All" -> results
        "Stocks" -> results.filter { it.type.equals("Equity", ignoreCase = true) }
        "F&O" -> results.filter { it.type.equals("Derivative", ignoreCase = true) }
        "MF" -> results.filter { it.type.equals("Mutual Fund", ignoreCase = true) }
        "ETF" -> results.filter { it.type.equals("ETF", ignoreCase = true) }
        "FAQs" -> results.filter { it.type.equals("FAQ", ignoreCase = true) }
        else -> results
    }
}

@Composable
fun RecentSearchesList(
    searches: List<RecentSearch>,
    onSymbolSelected: (String) -> Unit
) {
    LazyColumn {
        itemsIndexed(searches) { _, search ->
            RecentSearchItem(search, onSymbolSelected)
        }
    }
}

@Composable
fun RecentSearchItem(
    search: RecentSearch,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(search.symbol) }
            .padding(8.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = search.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = search.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SearchResultsList(
    results: List<SearchResult>,
    onSymbolSelected: (String) -> Unit
) {
    LazyColumn {
        itemsIndexed(results) { _, result ->
            SearchResultItem(result, onSymbolSelected)
        }
    }
}

@Composable
fun SearchResultItem(result: SearchResult, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(result.symbol) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = result.symbol,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = result.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Chip(content = result.region)
                Chip(content = result.currency)
                Chip(content = "Score: ${"%.2f".format(result.matchScore)}")
            }
        }
    }
}

@Composable
fun Chip(
    content: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.padding(end = 4.dp)
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
