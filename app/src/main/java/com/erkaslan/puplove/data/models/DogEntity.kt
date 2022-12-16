package com.erkaslan.puplove.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DogEntity (
    @PrimaryKey
    var pictureUri: String,

    @ColumnInfo(name = "breed_name")
    var breedName: String? = null,

    @ColumnInfo(name = "sub_breed_name")
    var subBreedName: String? = null,

    @ColumnInfo(name = "favorited")
    var favorited: Boolean = true,

    @ColumnInfo(name = "file_path")
    var filePath: String? = null
)