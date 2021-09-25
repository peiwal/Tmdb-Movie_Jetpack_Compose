package petrov.ivan.tmdb.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.database.FavoritesDatabaseDao
import petrov.ivan.tmdb.database.DBConverter

class FavoritesViewModel(private val database: FavoritesDatabaseDao, application: Application) : AndroidViewModel(application) {
    private val _favoritesList by lazy {
        MutableLiveData<List<TmdbMovie>>().also {
            loadData()
        }
    }
    val favoritesList: LiveData<List<TmdbMovie>> = _favoritesList

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            database.getAllRecords()?.collect {
                viewModelScope.launch(Dispatchers.Main) {
                    _favoritesList.value = it.map { DBConverter.convertToTmdbMovie(it) }
                }
            }
        }
    }
}