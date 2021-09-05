package petrov.ivan.tmdb.ui.search

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.service.TmdbApi
import timber.log.Timber

/**
 * this viewModel observes change
 * @see suggestText
 * and herself loades suggestions
 */
class SearchViewModel(private val movieService: TmdbApi, application: Application) : AndroidViewModel(application) {
    val suggestText = MutableLiveData<String>()

    private val _searchItems = MutableLiveData<List<TmdbMovie>>()
    val searchItems: LiveData<List<TmdbMovie>> = _searchItems

    private var lastTextSuggest: String? = null

    private val searchObserver = Observer<String> {
        loadSuggest(it)
    }

    init {
        suggestText.observeForever(searchObserver)
        Exception("").message
    }

    override fun onCleared() {
        suggestText.removeObserver(searchObserver)
    }

    private var job: Job? = null

    private fun loadSuggest(newTextSuggest: String?) {
        if (newTextSuggest == lastTextSuggest) {
            return
        }
        lastTextSuggest = newTextSuggest
        job?.cancel()

        if (TextUtils.isEmpty(newTextSuggest)) {
            _searchItems.value = ArrayList()
            return
        }

        job = viewModelScope.launch(Dispatchers.Main) {
            delay(500L) // debounce
            if (lastTextSuggest != newTextSuggest) return@launch

            val request = movieService.getMovieByQuery(newTextSuggest!!, getApplication<Application>().getString(R.string.response_language))

            try {
                val response = request.await()
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    val suggestions = movieResponse?.results
                    _searchItems.value = suggestions?.let{ArrayList(it.filter { it.backdropPath != null })} ?: ArrayList()
                } else Timber.i("loadSuggest: ${response.errorBody().toString()}")
            } catch (e: Exception){
                Timber.e("loadSuggest: $e")
            }
        }
    }
}