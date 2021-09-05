package petrov.ivan.tmdb.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.database.FavoritesDatabaseDao
import petrov.ivan.tmdb.ui.utils.launchOnIO
import petrov.ivan.tmdb.utils.MovieConverter

class FavoritesViewModel(private val database: FavoritesDatabaseDao, application: Application) : AndroidViewModel(application) {
    private val _favoritesList by lazy {
        MutableLiveData<List<TmdbMovie>>().also {
            loadData()
        }
    }
    val favoritesList: LiveData<List<TmdbMovie>> = _favoritesList

    private fun loadData() {
        viewModelScope.launchOnIO(runOnIO = ::getFavorites,
            resultOnMain =  {
                _favoritesList.value = it
            })
    }

    private fun getFavorites(): ArrayList<TmdbMovie> {
        val result = ArrayList<TmdbMovie>()
        database.getAllRecords()?.forEach {
            result.add(MovieConverter.convertToTmdbMovie(it))
        }
        return result
    }
}