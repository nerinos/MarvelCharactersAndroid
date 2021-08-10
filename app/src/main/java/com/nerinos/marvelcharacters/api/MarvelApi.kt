package com.nerinos.marvelcharacters.api

import com.nerinos.marvelcharacters.BuildConfig
import com.nerinos.marvelcharacters.utils.Utils.Companion.md5
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelApi {

    companion object {
        const val BASE_URL = "https://gateway.marvel.com/"
        const val API_KEY = BuildConfig.MARVEL_API_KEY
        const val PRIVATE_KEY = BuildConfig.MARVEL_PRIVATE_KEY

        fun getHash(ts: Long): String {
            return md5(ts.toString().plus(PRIVATE_KEY).plus(API_KEY))
        }
    }

    @GET("v1/public/characters")
    fun getCharacters(
        @Query("apikey") apikey: String = API_KEY,
        @Query("nameStartsWith") nameStartsWith: String?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int?,
        @Query("hash") hash: String,
        @Query("ts") ts: String
    ): Single<MarvelResponse>

}