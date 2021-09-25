package petrov.ivan.tmdb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PopularMoviesDatabaseDao {
    @Insert
    fun insert(movie: List<PopularMovieDB>)

    @Query("DELETE from popular_movies WHERE timeUntilWhichToStore < :current_timestamp ")
    fun deleteOlderRecords(current_timestamp: Long)

    @Query("SELECT * from popular_movies ORDER BY id_movie asc")
    fun getAllRecords(): Flow<List<PopularMovieDB>>
}