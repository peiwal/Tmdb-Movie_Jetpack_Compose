package petrov.ivan.tmdb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovieDB::class, PopularMovieDB::class], version = 3,  exportSchema = false)
abstract class TmdbDatabase : RoomDatabase() {
    abstract val favoritesDatabaseDao: FavoritesDatabaseDao
    abstract val popularMoviesDatabaseDao: PopularMoviesDatabaseDao

    companion object {

        @Volatile private var instance: TmdbDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            TmdbDatabase::class.java,
            "favorites_database")
            .fallbackToDestructiveMigration()
            .build()
    }
}