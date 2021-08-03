package com.nerinos.marvelcharacters.utils

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nerinos.marvelcharacters.BuildConfig
import com.nerinos.marvelcharacters.api.MarvelApi
import com.nerinos.marvelcharacters.data.db.CharactersDatabase
import com.nerinos.marvelcharacters.data.entities.MarvelCharacter
import com.nerinos.marvelcharacters.data.entities.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MarvelRemoteMediator(
    private val query: String,
    private val database: CharactersDatabase,
    private val api: MarvelApi
) : RemoteMediator<Int, MarvelCharacter>() {
    val dao = database.charactersDao()
    val remoteKeyDao = database.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MarvelCharacter>
    ): MediatorResult {
        return try {
            val remoteKey = database.withTransaction {
                remoteKeyDao.remoteKeyByQuery(query)
            }

            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {

                    if (remoteKey != null) {
                        if (remoteKey.nextKey == null) {
                            return MediatorResult.Success(
                                endOfPaginationReached = true
                            )
                        }
                        remoteKey.nextKey
                    } else null


                }
            }


            val ts = System.currentTimeMillis()
            val response = api.getCharacters(
                apikey = BuildConfig.MARVEL_API_KEY,
                nameStartsWith = if (!query.isEmpty()) query else null,
                limit = state.config.pageSize,
                offset = loadKey,
                ts = ts.toString(),
                hash = MarvelApi.getHash(ts)
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(query)
                    dao.deleteByQuery(query)
                }

                // Update RemoteKey for this query.
                val nextKey =
                    if (remoteKey?.nextKey != null) (remoteKey.nextKey + state.config.pageSize) else (state.config.pageSize)
                remoteKeyDao.insertOrReplace(
                    RemoteKey(query, nextKey)
                )

                dao.insertAll(response.data.results)
            }


            MediatorResult.Success(
                endOfPaginationReached = response.data.results.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}