package petrov.ivan.tmdb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDatabaseDao {
    @Insert
    fun insert(movie: FavoriteMovieDB)

    @Query("DELETE from favorites_movie WHERE id_imgb = :id_imdb ")
    fun delete(id_imdb: Int)

    @Query("SELECT * from favorites_movie ORDER BY id_movie desc")
    fun getAllRecords(): Flow<List<FavoriteMovieDB>>?

    @Query("SELECT * from favorites_movie WHERE id_imgb = :id_imdb")
    fun getMovieFlowById(id_imdb: Int): Flow<FavoriteMovieDB>
}