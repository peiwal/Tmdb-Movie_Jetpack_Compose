package petrov.ivan.tmdb.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import petrov.ivan.tmdb.AppConstants
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.ui.theme.TmdbTheme
import timber.log.Timber

@Composable
fun MovieView(modifier: Modifier = Modifier,
              movie: TmdbMovie,
              onMovieClick: (TmdbMovie) -> Unit,
              isShortOverview: Boolean = false) {
    TmdbSurface(modifier = modifier) {
        MovieView(movie = movie, onMovieClick = onMovieClick, isShortOverview = isShortOverview)
    }
}

@Composable
private fun MovieView(movie: TmdbMovie, onMovieClick: (TmdbMovie) -> Unit, isShortOverview: Boolean = false) {
    Column(modifier = Modifier
        .fillMaxWidth(
            fraction = when (LocalConfiguration.current.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    0.7f
                }
                else -> 1f
            }
        )
        .clickable(enabled = isShortOverview) { onMovieClick.invoke(movie) }
        .padding(
            bottom = 16.dp
        )
    ) {
        Image(
            painter = rememberImagePainter(
                data = AppConstants.TMDB_PHOTO_URL + movie.backdropPath,
                builder = {
                    crossfade(true)
                    placeholder(drawableResId = R.drawable.filmstrip)
                }
            ),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h6
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.rating_pref),
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.subtitle1,
                    color = TmdbTheme.colors.textCaption
                )

                Text(
                    text = movie.voteAverage.toString(),
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.subtitle1,
                    color = TmdbTheme.colors.textCaption
                )
                Text(
                    text = getReleaseYear(movie.releaseDate),
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.subtitle1,
                    color = TmdbTheme.colors.textCaption
                )
            }

            if (movie.overview.isNotEmpty()) {
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = if (isShortOverview) 3 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun getReleaseYear(releaseDate: String): String {
    return try {
        releaseDate.split("-").first()
    } catch (e: Exception) {
        Timber.e("error parse releaseDate")
        ""
    }
}