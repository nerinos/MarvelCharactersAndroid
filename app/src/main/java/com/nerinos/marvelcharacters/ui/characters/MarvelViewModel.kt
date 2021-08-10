package com.nerinos.marvelcharacters.ui.characters

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.nerinos.marvelcharacters.repositories.MarvelRepository

class MarvelViewModel @ViewModelInject constructor(
    private val repository: MarvelRepository,
    @Assisted state: SavedStateHandle
) : ViewModel() {


    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val characters = currentQuery.switchMap {
            queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)


    }


    fun searchCharacters(query:String) {
        if (query.isEmpty() || query.length >= 3) {
            currentQuery.value = query
        }
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = ""
    }
}