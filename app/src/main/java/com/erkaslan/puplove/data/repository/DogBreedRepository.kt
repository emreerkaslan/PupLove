package com.erkaslan.puplove.data.repository

import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.util.Result

interface DogBreedRepository {
    suspend fun getAllBreeds() : Result<List<String>>
    suspend fun getBreedPictures(breedName: String) : Result<List<DogEntity>>
    suspend fun updateFavoriteStatus(entity: DogEntity) : Result<Unit>
    suspend fun getAllFavorites() : List<DogEntity>
    suspend fun deleteFavorite(dogEntity: DogEntity)
}