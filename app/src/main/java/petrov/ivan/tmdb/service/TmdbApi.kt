package petrov.ivan.tmdb.service

import kotlinx.coroutines.Deferred
import petrov.ivan.tmdb.data.TmdbMovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi{

    @GET("movie/popular")
    suspend fun getPopularMovie(@Query("language") language: String): Response<TmdbMovieResponse>

    @GET("search/movie")
    suspend fun getMovieByQuery(@Query("query") query: String, @Query("language") language: String): Response<TmdbMovieResponse>
}