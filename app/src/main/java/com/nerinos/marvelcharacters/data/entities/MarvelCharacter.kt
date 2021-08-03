package com.nerinos.marvelcharacters.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_characters")
data class MarvelCharacter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    @Embedded
    val thumbnail: MarvelThumbnail
) {
    data class MarvelThumbnail(
        val path: String,
        val extension: String
    )

}