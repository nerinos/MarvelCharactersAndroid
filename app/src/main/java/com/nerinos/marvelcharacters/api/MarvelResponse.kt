package com.nerinos.marvelcharacters.api

import com.nerinos.marvelcharacters.data.entities.MarvelCharacter

data class MarvelResponse (
    val data: MarvelResponseData

) {
    class MarvelResponseData(
        val offset: Int,
        val total: Int,
        val results: List<MarvelCharacter>
    )
}