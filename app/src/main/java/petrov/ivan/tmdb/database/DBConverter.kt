package petrov.ivan.tmdb.database

import petrov.ivan.tmdb.data.TmdbMovie

object DBConverter {
    fun convertToTmdbMovie(movie: FavoriteMovieDB): TmdbMovie {
        return TmdbMovie(movie.imdbId,
            movie.voteAverage,
            movie.voteCount,
            movie.title,
            movie.overview,
            movie.adult,
            movie.video,
            movie.posterPath,
            movie.releaseDate,
            movie.backdropPath)
    }

    fun convertToFavoriteMovieDB(tmdbMovie: TmdbMovie): FavoriteMovieDB {
        return FavoriteMovieDB(tmdbMovie.id,
            tmdbMovie.voteAverage,
            tmdbMovie.voteCount,
            tmdbMovie.title,
            tmdbMovie.overview,
            tmdbMovie.adult,
            tmdbMovie.video,
            tmdbMovie.posterPath,
            tmdbMovie.backdropPath,
            tmdbMovie.releaseDate)
    }

    fun convertToTmdbMovie(movie: PopularMovieDB): TmdbMovie {
        return TmdbMovie(movie.imdbId,
            movie.voteAverage,
            movie.voteCount,
            movie.title,
            movie.overview,
            movie.adult,
            movie.video,
            movie.posterPath,
            movie.releaseDate,
            movie.backdropPath)
    }

    fun convertToPopularMovieDB(tmdbMovie: TmdbMovie, timeUntilWhichToStore: Long): PopularMovieDB {
        return PopularMovieDB(tmdbMovie.id,
            tmdbMovie.voteAverage,
            tmdbMovie.voteCount,
            tmdbMovie.title,
            tmdbMovie.overview,
            tmdbMovie.adult,
            tmdbMovie.video,
            tmdbMovie.posterPath,
            tmdbMovie.backdropPath,
            tmdbMovie.releaseDate,
            timeUntilWhichToStore = timeUntilWhichToStore
        )
    }
}