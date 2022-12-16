package com.erkaslan.puplove.data.db

import androidx.room.*
import com.erkaslan.puplove.data.models.DogEntity

@Dao
interface DogEntityDao {
    @Upsert
    fun upsert(dogEntity: DogEntity)

    @Delete
    fun delete(dogEntity: DogEntity)

    @Query("SELECT * FROM dogEntity")
    fun getAll(): List<DogEntity>

    @Query("SELECT * FROM dogEntity WHERE favorited = true")
    fun getAllFavorites(): List<DogEntity>
}