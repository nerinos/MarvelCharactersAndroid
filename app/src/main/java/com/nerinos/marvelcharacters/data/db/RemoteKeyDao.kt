package com.nerinos.marvelcharacters.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nerinos.marvelcharacters.data.entities.RemoteKey
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE label = :query")
    fun remoteKeyByQuery(query: String): Single<RemoteKey>

    @Query("DELETE FROM remote_keys WHERE label = :query")
    fun deleteByQuery(query: String)
}