package petrov.ivan.tmdb.ui.popularMovies

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.data.TmdbMovie
import petrov.ivan.tmdb.database.DBConverter
import petrov.ivan.tmdb.database.PopularMoviesDatabaseDao
import petrov.ivan.tmdb.repository.*
import petrov.ivan.tmdb.service.TmdbApi
import timber.log.Timber
import java.util.concurrent.TimeUnit
import petrov.ivan.tmdb.repository.LoadingStatus.*

class PopularMoviesViewModel(private val movieService: TmdbApi,
                             private val popularMoviesDatabaseDao: PopularMoviesDatabaseDao,
                             application: Application) : AndroidViewModel(application) {

    private val storageTimeMinute = 1L
    private val repositoryStorageTimeMS = TimeUnit.MINUTES.toMillis(storageTimeMinute)

    private val _viewIntent: MutableLiveData<PopularMovieIntent> = MutableLiveData(LoadingIntent(null))
    val viewIntent = _viewIntent

    private val repository = object: RepositoryBase<List<TmdbMovie>, Any?, List<TmdbMovie>>(viewModelScope) {
        override fun saveNetworkResult(item: List<TmdbMovie>?) {
            Timber.e("saveNetworkResult")
            item?.let {
                popularMoviesDatabaseDao.insert(it.map {
                    DBConverter.convertToPopularMovieDB(
                        it,
                        timeUntilWhichToStore = getCurrentTimeMillis() + repositoryStorageTimeMS
                    )
                })
            }
        }

        override fun shouldFetch(data: List<TmdbMovie>?) = data == null || data.isEmpty()

        override fun loadFromDb(request: Any?): Flow<List<TmdbMovie>> {
            Timber.e("loadFromDb")
            popularMoviesDatabaseDao.deleteOlderRecords(getCurrentTimeMillis())
            return popularMoviesDatabaseDao.getAllRecords()
                .map { it.map { DBConverter.convertToTmdbMovie(it) } }
        }

        override suspend fun loadFromNetwork(request: Any?): ApiResponse<List<TmdbMovie>> {
            Timber.e("loadFromNetwork")
            return try {
                val response = movieService.getPopularMovie(getApplication<Application>().getString(R.string.response_language))
                if (response.isSuccessful) {
                    ApiSuccessResponse(response.body()?.results ?: ArrayList(), null)
                } else {
                    ApiErrorResponse(application.getString(R.string.error_load_data))
                }
            } catch (e: Throwable) {
                Timber.e("error loadFromNetwork ${e}")
                ApiErrorResponse(application.getString(R.string.error_load_data))
            }
        }

        private fun getCurrentTimeMillis() = System.currentTimeMillis()
    }

    init {
        repository.asLiveData().observeForever {
            _viewIntent.value = when(it.status) {
                ERROR -> ErrorIntent(it.data, it.message)
                LOADING -> LoadingIntent(it.data)
                SUCCESS -> SuccessIntent(it.data)
            }
        }
        loadData()
    }

    fun loadData() {
        repository.fetch()
    }

    fun showedError() {
        _viewIntent.value = SuccessIntent(_viewIntent.value?.data)
    }
}