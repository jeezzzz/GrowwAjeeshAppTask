@file:OptIn(ExperimentalAnimationApi::class)

package `in`.groww.ajeeshapptask.ui.presentation.explore.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import `in`.groww.ajeeshapptask.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import `in`.groww.ajeeshapptask.domain.model.utils.RecentSearch
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.Stock
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.TopGainersTopLosersResponse
import `in`.groww.ajeeshapptask.ui.components.SectionHeader
import `in`.groww.ajeeshapptask.ui.components.StockGrid
import `in`.groww.ajeeshapptask.ui.presentation.explore.viewmodel.ExploreViewModel
import `in`.groww.ajeeshapptask.ui.utils.ErrorState
import `in`.groww.ajeeshapptask.ui.utils.FullScreenLoader
import `in`.groww.ajeeshapptask.ui.utils.Resource

@Composable
fun ExploreScreen(
    navController: NavHostController,
    viewModel: ExploreViewModel = hiltViewModel(),
    onThemeToggle: () -> Unit,
    onViewAllClick: (String) -> Unit,
    onStockClick: (String) -> Unit,
    isDarkMode: Boolean
) {
    val state = viewModel.state.collectAsState().value
    val recentSearches = viewModel.recentSearches.collectAsState().value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state) {
            is Resource.Loading -> FullScreenLoader()
            is Resource.Error -> {
                val errorState = state
                ErrorState(message = errorState.message)
            }
            is Resource.Success<*> -> {
                val successState = state as Resource.Success<TopGainersTopLosersResponse>
                ExploreContent(
                    data = successState.data,
                    navController = navController,
                    onThemeToggle = onThemeToggle,
                    recentSearches = recentSearches,
                    onViewAllClick = onViewAllClick,
                    isDarkMode = isDarkMode,
                    onStockClick = onStockClick
                )
            }
        }

    }
}

@Composable
private fun ExploreContent(
    data: TopGainersTopLosersResponse,
    recentSearches: List<RecentSearch>,
    navController: NavHostController,
    onThemeToggle: () -> Unit,
    onViewAllClick: (String) -> Unit,
    onStockClick: (String) -> Unit,
    isDarkMode: Boolean
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        AppBar(
            isDarkMode = isDarkMode,
            onThemeToggle = onThemeToggle,
            onSearchClick = { navController.navigate("search") }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            RecentSearchesSection(
                recentSearches = recentSearches,
                navController=navController
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Top Gainers Section
            SectionHeader(
                title = "Top Gainers (${data.topGainers.size})",
                onViewAllClick = { onViewAllClick("gainers") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            StockGrid(
                stocks = data.topGainers,
                onStockClick = { onStockClick(it.ticker) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Top Losers Section
            SectionHeader(
                title = "Top Losers (${data.topLosers.size})",
                onViewAllClick = { onViewAllClick("losers") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            StockGrid(
                stocks = data.topLosers,
                onStockClick = { onStockClick(it.ticker) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // most actively Traded
            SectionHeader(
                title = "Most Actively Traded (${data.mostActivelyTraded.size})",
                onViewAllClick = { onViewAllClick("mat") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            StockGrid(
                stocks = data.mostActivelyTraded,
                onStockClick = { onStockClick(it.ticker) }
            )
        }
//        SectionHeader(
//                title = "Key Features",
//        onViewAllClick = { /* Optional */ }
//        )
//
//        FeatureGrid(
//            onEarningsClick = {
//                navController.navigate("earnings/AAPL")
//            },
//            onNewsClick = { navController.navigate("news/AAPL") }
//        )

    }
}

@Composable
fun FeatureGrid(
    onEarningsClick: () -> Unit,
    onNewsClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp).heightIn(max = 400.dp)
    ) {
        item {
            FeatureCard(
                title = "Earnings Calendar",
                icon = Icons.Default.Event,
                onClick = onEarningsClick
            )
        }
        item {
            FeatureCard(
                title = "Market News",
                icon = Icons.Default.Book,
                onClick = onNewsClick
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
private fun AppBar(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                spotColor = MaterialTheme.colorScheme.outline
            )
            .background(MaterialTheme.colorScheme.background)
             .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top=8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.groww_icon),
                contentDescription = "Groww Logo",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        //heading
        Text(
            text = "Stock",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        // Search Bar (Icon)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .height(30.dp)
                .clickable { onSearchClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        val rotation by animateFloatAsState(
            targetValue = if (isDarkMode) 180f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "ThemeToggleRotation"
        )

        IconButton(
            onClick = onThemeToggle,
            modifier = Modifier
                .size(30.dp)
                .padding(end = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = isDarkMode,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) + scaleIn() with
                                fadeOut(animationSpec = tween(300)) + scaleOut()
                    },
                    modifier = Modifier.rotate(rotation)
                ) { dark ->
                    if (dark) {
                        Icon(
                            imageVector = Icons.Filled.WbSunny,
                            contentDescription = "Light Mode",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.NightsStay,
                            contentDescription = "Dark Mode",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        //profile
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "U",
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }

    }
}

@Composable
private fun RecentSearchesSection(
    recentSearches: List<RecentSearch>,
    navController: NavHostController,
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recent Searches",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (recentSearches.isEmpty()) {
            Text(
                text = "No recent searches",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        } else {
            Column {
                recentSearches.take(3).forEach { search ->
                    RecentSearchItem(search.symbol, search.name, onSearchClick = { symbol ->
                        navController.navigate("detail/$symbol")
                    })

                }
            }
        }
    }
}


@Composable
private fun RecentSearchItem(
    symbol: String,
    name: String,
    onSearchClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSearchClick(symbol) }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = symbol,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
    }
}
