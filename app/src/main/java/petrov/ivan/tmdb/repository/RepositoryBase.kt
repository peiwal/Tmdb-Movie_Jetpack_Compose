package petrov.ivan.tmdb.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import petrov.ivan.tmdb.BuildConfig

/**
 * @param ResultType returned type
 * @param RequestType type for network request param
 * @param ResponseType type returned from network request
 */
abstract class RepositoryBase<ResultType, RequestType, ResponseType>
@MainThread constructor(private val coroutineScope: CoroutineScope) {

    private val result = MutableLiveData<LoadingState<ResultType>>(LoadingState.loading(null))
    private var job: Job? = null

    fun asLiveData() = result as LiveData<LoadingState<ResultType>>

    fun fetch(request: RequestType? = null) {
        setValue(LoadingState.loading(result.value?.data))

        job?.cancel()
        job = coroutineScope.launch(Dispatchers.IO) {
            if (BuildConfig.DEBUG) delay(1000L) // imitation of work

            loadFromDb(request).collect {
                if (shouldFetch(it)) {
                    fetchFromNetwork(request, it)
                } else {
                    setValue(LoadingState.success(it))
                }
            }
        }
    }

    private fun setValue(newValue: LoadingState<ResultType>) {
        coroutineScope.launch(Dispatchers.Main) {
            if (result.value != newValue) result.value = newValue
        }
    }

    private suspend fun fetchFromNetwork(request: RequestType?, latestResult: ResultType?) {
        when (val response = loadFromNetwork(request)) {
            is ApiSuccessResponse -> {
                coroutineScope.launch(Dispatchers.IO) {
                    saveNetworkResult(response.body)
                }
            }
            is ApiEmptyResponse -> {
                setValue(LoadingState.success(latestResult))
            }
            is ApiErrorResponse -> {
                onFetchFailed()
                setValue(LoadingState.error(response.errorMessage, latestResult))
            }
        }
    }

    protected open fun onFetchFailed() {}

    @WorkerThread
    protected abstract fun saveNetworkResult(item: ResponseType?)

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @WorkerThread
    protected abstract fun loadFromDb(request: RequestType?): Flow<ResultType>

    @WorkerThread
    protected abstract suspend fun loadFromNetwork(request: RequestType?): ApiResponse<ResponseType>
}
