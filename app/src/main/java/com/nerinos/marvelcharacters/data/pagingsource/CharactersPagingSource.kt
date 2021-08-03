package com.nerinos.marvelcharacters.data.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import com.nerinos.marvelcharacters.BuildConfig.MARVEL_API_KEY
import com.nerinos.marvelcharacters.api.MarvelApi
import com.nerinos.marvelcharacters.api.MarvelApi.Companion.getHash
import com.nerinos.marvelcharacters.data.entities.MarvelCharacter
import java.lang.Exception


private const val MARVEL_STARTING_PAGE_INDEX = 0

class CharactersPagingSource(
    private val api: MarvelApi,
    private val query: String
) : PagingSource<Int, MarvelCharacter>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MarvelCharacter> {
        val position = params.key ?: MARVEL_STARTING_PAGE_INDEX
        return try {
            val ts = System.currentTimeMillis()
            var nameStartsWith: String? = null
            if (!query.isEmpty()) nameStartsWith = query
            val response = api.getCharacters(
                apikey = MARVEL_API_KEY,
                nameStartsWith = nameStartsWith,
                limit = params.loadSize,
                offset = position,
                ts = ts.toString(),
                hash = getHash(ts)
            )
            val charactersData = response.data
            val characters = charactersData.results

            LoadResult.Page(
                data = characters,
                prevKey = if (position == MARVEL_STARTING_PAGE_INDEX) null else position - params.loadSize,
                nextKey = if (characters.isEmpty()) null else position + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}