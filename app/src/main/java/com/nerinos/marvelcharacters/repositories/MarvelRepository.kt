package com.nerinos.marvelcharacters.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.nerinos.marvelcharacters.api.MarvelApi
import com.nerinos.marvelcharacters.data.db.CharactersDatabase
import com.nerinos.marvelcharacters.utils.MarvelRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarvelRepository @Inject constructor(
    private val marvelApi: MarvelApi,
    private val database: CharactersDatabase
){
    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
            ),
//            pagingSourceFactory = { CharactersPagingSource(marvelApi, query) }
            remoteMediator = MarvelRemoteMediator(query, database, marvelApi)
        ) {
            database.charactersDao().pagingSource(query)
        }.liveData

}