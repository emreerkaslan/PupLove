package com.erkaslan.puplove.data.db

import androidx.room.*
import com.erkaslan.puplove.data.models.DogEntity

@Dao
interface DogEntityDao {
    @Upsert
    suspend fun upsert(dogEntity: DogEntity)

    @Delete
    suspend fun delete(dogEntity: DogEntity)

    @Query("SELECT * FROM dogEntity")
    suspend fun getAll(): List<DogEntity>

    @Query("SELECT * FROM dogEntity WHERE favorited = true")
    suspend fun getAllFavorites(): List<DogEntity>
}