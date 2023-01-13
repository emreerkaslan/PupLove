package com.erkaslan.puplove.data.repository

import com.erkaslan.puplove.data.db.DogEntityDao
import com.erkaslan.puplove.data.models.DogBreedListResponse
import com.erkaslan.puplove.data.models.DogBreedPictureListResponse
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.data.services.DogBreedService
import com.erkaslan.puplove.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DogBreedRepositoryImpl @Inject constructor(private val dogEntityDao: DogEntityDao, private val dogBreedService: DogBreedService) : DogBreedRepository {

    override suspend fun getAllBreeds(): Result<List<String>> =
        when (val response = handleApi { dogBreedService.getAllBreeds() }) {
            is ApiSuccess -> {
                val list = (response.data as? DogBreedListResponse)?.asBreedListOrNull() ?: listOf()
                Success(list)
            }
            is ApiError -> {
                Failed(Throwable(response.message))
            }
            is ApiFailed -> {
                Failed(Throwable(response.exception.message))
            }
        }

    override suspend fun getBreedPictures(breedName: String): Result<List<DogEntity>> =
        when (val response = handleApi { dogBreedService.getBreedPictures(breedName) }) {
            is ApiSuccess -> {
                val list = (response.data as? DogBreedPictureListResponse)?.message ?: listOf()
                val allFavorites = dogEntityDao.getAll()
                Success(list.mergeWithFavorites(breedName, allFavorites))
            }
            is ApiError -> {
                Failed(Throwable(response.message))
            }
            is ApiFailed -> {
                Failed(Throwable(response.exception.message))
            }
        }

    override suspend fun updateFavoriteStatus(entity: DogEntity) = if (entity.favorited) dogEntityDao.upsert(entity) else dogEntityDao.delete(entity)

    override suspend fun getAllFavorites(): List<DogEntity> = dogEntityDao.getAllFavorites().reversed()

    override suspend fun deleteFavorite(dogEntity: DogEntity) = dogEntityDao.delete(dogEntity)
}