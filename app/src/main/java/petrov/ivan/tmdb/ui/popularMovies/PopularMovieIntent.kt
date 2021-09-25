package petrov.ivan.tmdb.ui.popularMovies

import petrov.ivan.tmdb.data.TmdbMovie

// mvi intents
sealed class PopularMovieIntent(val data: List<TmdbMovie>?)
class LoadingIntent(data: List<TmdbMovie>?): PopularMovieIntent(data)
class SuccessIntent(data: List<TmdbMovie>?): PopularMovieIntent(data)
class ErrorIntent(data: List<TmdbMovie>?, val message: String?): PopularMovieIntent(data)