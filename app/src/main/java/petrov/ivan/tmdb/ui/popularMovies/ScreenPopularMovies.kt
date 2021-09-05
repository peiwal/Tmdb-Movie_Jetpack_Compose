package petrov.ivan.tmdb.ui.popularMovies

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.ui.components.MovieView

@Composable
fun ScreenPopularMovies(viewModel: PopularMoviesViewModel, onMovieClick: (TmdbMovie) -> Unit, scaffoldState: ScaffoldState) {
    val movies by viewModel.movieList.observeAsState()
    val scrollState = rememberLazyListState()
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val isLoadingError by viewModel.isLoadingError.observeAsState(false)
    val statusBarPaddingValues = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars)

    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.loadData() },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                refreshingOffset = statusBarPaddingValues.calculateTopPadding() + 16.dp
            )
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            state = scrollState,
            contentPadding = statusBarPaddingValues,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(movies ?: listOf(),
                key = { movie -> movie.id })
            { movie ->
                MovieView(
                    movie = movie,
                    isShortOverview = true,
                    onMovieClick = onMovieClick
                )
            }
        }
        if (isLoadingError) {
            val scope = rememberCoroutineScope()
            val message = stringResource(id = R.string.error_load_data)
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(message)
                    viewModel.showedError()
                }
            }
        } else {
            LaunchedEffect(true) {
                if (movies == null || movies?.isEmpty() == true) viewModel.loadData()
            }
        }
    }
}