package petrov.ivan.tmdb.ui.movieInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.ui.components.MovieView
import petrov.ivan.tmdb.ui.components.TmdbScaffold
import petrov.ivan.tmdb.ui.components.TmdbSurface
import petrov.ivan.tmdb.ui.theme.TmdbTheme
import petrov.ivan.tmdb.ui.utils.mirroringBackIcon

@Composable
fun ScreenMovieInfo(viewModel: MovieInfoViewModel, upPress: () -> Unit) {
    val isFavorite by viewModel.isFavorite.observeAsState()
    val isFavoriteChanged by viewModel.isFavoriteChanged.observeAsState(false)
    val scaffoldState = rememberScaffoldState()

    TmdbScaffold(
        modifier = Modifier,
        floatingActionButton = { isFavorite?.let { isFavorite ->
            FavoriteFab(
                upPress = {
                    // dismiss others
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()

                    if (isFavorite) viewModel.removeFromFavorite()
                    else viewModel.addToFavorite()
                },
                isFavorite = isFavorite
            )}
        },
        scaffoldState = scaffoldState,
    ) { innerPaddingModifier ->
        TmdbSurface {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                val scrollState = rememberScrollState()

                MovieView(
                    movie = viewModel.movie,
                    onMovieClick = {},
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .systemBarsPadding()
                        .statusBarsPadding()
                )
            }
            Up(upPress = upPress)
        }
    }

    if (isFavoriteChanged) {
        isFavorite?.let {
            ShowSnackBarFavoriteChanged(isFavorite = it,
                scaffoldState = scaffoldState,
                isFavoriteChanged = viewModel.isFavoriteChanged)
        }
    }
}

@Composable
private fun ShowSnackBarFavoriteChanged(isFavorite: Boolean, scaffoldState: ScaffoldState, isFavoriteChanged: MutableLiveData<Boolean>) {
    val scope = rememberCoroutineScope()
    val message = stringResource(id = if (isFavorite) R.string.label_added_to_favorite else R.string.label_removed_from_favorite)
    LaunchedEffect(scaffoldState.snackbarHostState, isFavorite) {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message)
            isFavoriteChanged.value = false
        }
    }
}

@Composable
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .size(40.dp)
            .background(
                color = TmdbTheme.colors.iconPrimary.copy(alpha = 0.9f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = mirroringBackIcon(),
            tint = TmdbTheme.colors.iconInteractive,
            contentDescription = stringResource(R.string.label_back)
        )
    }
}

@Composable
private fun FavoriteFab(upPress: () -> Unit, isFavorite: Boolean) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .navigationBarsPadding(bottom = true)
            .size(40.dp)
            .background(
                color = TmdbTheme.colors.iconPrimary.copy(0.9f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Outlined.Delete else Icons.Outlined.Star,
            tint = TmdbTheme.colors.iconInteractive,
            contentDescription = stringResource(if (isFavorite) R.string.label_remove_favorite else R.string.label_add_favorite)
        )
    }
}