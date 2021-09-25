package petrov.ivan.tmdb.ui.movieInfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.database.FavoritesDatabaseDao
import petrov.ivan.tmdb.database.DBConverter

class MovieInfoViewModel(private val database: FavoritesDatabaseDao, application: Application, val movie: TmdbMovie) : AndroidViewModel(application) {
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    val isFavoriteChanged = MutableLiveData(false)

    init {
        viewModelScope.launch {
            // observe flow
            database.getMovieFlowById(movie.id).map { it != null }.collect { isFoundInFavorite ->
                isFavoriteChanged.value = isFavorite.value != null && isFavorite.value != isFoundInFavorite
                _isFavorite.value = isFoundInFavorite
            }
        }
    }

    fun addToFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(
                DBConverter.convertToFavoriteMovieDB(movie)
            )
        }
    }

    fun removeFromFavorite() {
        viewModelScope.launch(Dispatchers.IO) { database.delete(movie.id) }
    }
}