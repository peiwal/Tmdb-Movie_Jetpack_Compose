package petrov.ivan.tmdb.ui.favorites

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.ui.components.MovieView
import petrov.ivan.tmdb.ui.components.TmdbSurface
import petrov.ivan.tmdb.ui.theme.TmdbTheme

@Composable
fun ScreenFavorites(viewModel: FavoritesViewModel,
                    onMovieClick: (TmdbMovie) -> Unit,
                    modifier: Modifier = Modifier
) {
    TmdbSurface(modifier = modifier) {
        Box {
            ScreenFavorites(viewModel = viewModel, onMovieClick = onMovieClick)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun ScreenFavorites(viewModel: FavoritesViewModel, onMovieClick: (TmdbMovie) -> Unit) {
    val movies by viewModel.favoritesList.observeAsState()
    val scrollState = rememberLazyListState()
    val statusBarPaddingValues = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars)

    if (movies?.isEmpty() == true) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.favorites_empty),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = TmdbTheme.colors.textCaption
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
    }
}