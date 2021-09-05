package petrov.ivan.tmdb.ui.popularMovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.service.TmdbApi
import timber.log.Timber

class PopularMoviesViewModel(private val movieService: TmdbApi, application: Application) : AndroidViewModel(application) {

    private val _movieList = MutableLiveData<List<TmdbMovie>>()
    val movieList: LiveData<List<TmdbMovie>> = _movieList

    private val _isLoadingError = MutableLiveData<Boolean>()
    val isLoadingError: LiveData<Boolean> = _isLoadingError

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun loadData() {
        viewModelScope.launch(Dispatchers.Main) {
            _isRefreshing.value = true

            val request = movieService.getPopularMovie(getApplication<Application>().getString(R.string.response_language))
            try {
                val response = request.await()
                if(response.isSuccessful){
                    _movieList.value = response.body()?.results ?: ArrayList()
                } else {
                    Timber.i("loadData ${response.errorBody().toString()}")
                }
            } catch (e: Throwable){
                Timber.e("loadData ${e}")
                _isLoadingError.value = true
            }

            _isRefreshing.value = false
        }
    }

    fun showedError() {
        _isLoadingError.value = false
    }
}