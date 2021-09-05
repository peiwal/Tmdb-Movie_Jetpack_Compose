package petrov.ivan.tmdb.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.ui.components.MovieView
import petrov.ivan.tmdb.ui.components.SearchView
import petrov.ivan.tmdb.ui.components.TmdbSurface

@Composable
fun ScreenSearch(viewModel: SearchViewModel,
                 onMovieClick: (TmdbMovie) -> Unit,
                 modifier: Modifier = Modifier
) {
    TmdbSurface(modifier = modifier.statusBarsPadding()) {
        Box {
            ScreenSearch(viewModel = viewModel, onMovieClick = onMovieClick)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun ScreenSearch(viewModel: SearchViewModel, onMovieClick: (TmdbMovie) -> Unit) {
    Column {
        SearchView(mutableViewModelSearch = viewModel.suggestText)

        val scrollState = rememberLazyListState()
        val movies by viewModel.searchItems.observeAsState()

        LazyColumn(modifier = Modifier.fillMaxSize(),
            state = scrollState,
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
    }
}