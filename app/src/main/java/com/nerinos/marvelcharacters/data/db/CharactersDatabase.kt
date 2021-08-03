package com.nerinos.marvelcharacters.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nerinos.marvelcharacters.data.entities.MarvelCharacter
import com.nerinos.marvelcharacters.data.entities.RemoteKey

@Database(entities = [MarvelCharacter::class, RemoteKey::class], version = 1)
abstract class CharactersDatabase : RoomDatabase() {

    abstract fun charactersDao(): CharactersDao
    abstract fun remoteKeyDao(): RemoteKeyDao

}