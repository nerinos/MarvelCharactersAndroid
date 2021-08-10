package com.nerinos.marvelcharacters.utils

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxRemoteMediator
import com.nerinos.marvelcharacters.BuildConfig
import com.nerinos.marvelcharacters.api.MarvelApi
import com.nerinos.marvelcharacters.api.MarvelResponse
import com.nerinos.marvelcharacters.data.db.CharactersDatabase
import com.nerinos.marvelcharacters.data.entities.MarvelCharacter
import com.nerinos.marvelcharacters.data.entities.RemoteKey
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.Result.response





@OptIn(ExperimentalPagingApi::class)
class MarvelRemoteMediator(
    private val query: String,
    private val database: CharactersDatabase,
    private val api: MarvelApi
) : RxRemoteMediator<Int, MarvelCharacter>() {
    val dao = database.charactersDao()
    val remoteKeyDao = database.remoteKeyDao()

    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, MarvelCharacter>
    ): Single<MediatorResult> {

        val remoteKeySingle: Single<RemoteKey> = when (loadType) {
            LoadType.REFRESH -> Single.just(RemoteKey(query, null))
            LoadType.PREPEND -> return Single.just(MediatorResult.Success(false));
            LoadType.APPEND ->  {
                remoteKeyDao.remoteKeyByQuery(query).onErrorReturn { RemoteKey(query, null) }
            }
        }


        return remoteKeySingle
            .subscribeOn(Schedulers.io())
            .flatMap { remoteKey ->

                val ts = System.currentTimeMillis()
                return@flatMap api.getCharacters(
                    apikey = BuildConfig.MARVEL_API_KEY,
                    nameStartsWith = if (!query.isEmpty()) query else null,
                    limit = state.config.pageSize,
                    offset = remoteKey.nextKey,
                    ts = ts.toString(),
                    hash = MarvelApi.getHash(ts)
                )
                    .onErrorReturn {
                        MarvelResponse(MarvelResponse.MarvelResponseData(-1, 0, emptyList()))
                    }
                    .map { response ->
                        Log.e("SAMPLE_TAG", "response: ${response.data}")
                    if (response.data.offset == -1) return@map MediatorResult.Error(Throwable())
                    database.runInTransaction {
                        if (loadType == LoadType.REFRESH) {
                            remoteKeyDao.deleteByQuery(query)
                            dao.deleteByQuery(query)
                        }
                        val pageSize = if (state.config.pageSize > response.data.total) response.data.total else state.config.pageSize
                        val nextKey = if (remoteKey?.nextKey != null && response.data.results.isNotEmpty()) (remoteKey.nextKey + pageSize) else (pageSize)

                        remoteKeyDao.insertOrReplace(
                            RemoteKey(query, nextKey)
                        )

                        dao.insertAll(response.data.results)
                    }
                    return@map MediatorResult.Success(response.data.results.isEmpty())
                }
            }

    }

    override fun initializeSingle(): Single<InitializeAction> {
        return Single.just(InitializeAction.SKIP_INITIAL_REFRESH)
    }
}