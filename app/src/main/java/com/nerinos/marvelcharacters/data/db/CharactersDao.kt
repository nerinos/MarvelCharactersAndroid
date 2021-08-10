package com.nerinos.marvelcharacters.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.nerinos.marvelcharacters.data.entities.MarvelCharacter

@Dao
interface CharactersDao {

    @Query("SELECT * FROM table_characters WHERE name LIKE :query || '%' ORDER BY name ")
    fun pagingSource(query: String): PagingSource<Int, MarvelCharacter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(character: List<MarvelCharacter>)

    @Query("DELETE FROM table_characters WHERE id = :query")
    fun deleteByQuery(query: String)

}