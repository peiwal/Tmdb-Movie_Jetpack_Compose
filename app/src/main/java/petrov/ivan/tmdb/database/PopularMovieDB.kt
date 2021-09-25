package petrov.ivan.tmdb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "popular_movies")
data class PopularMovieDB(
    @ColumnInfo(name = "id_imgb")
    val imdbId: Int,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double,
    @ColumnInfo(name = "vote_count")
    val voteCount: Int,
    val title: String,
    val overview: String,
    val adult: Boolean,
    val video: Boolean,
    @ColumnInfo(name = "poster_path")
    val posterPath: String,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String,
    val timeUntilWhichToStore: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_movie")
    var movieId: Long = 0L
}
