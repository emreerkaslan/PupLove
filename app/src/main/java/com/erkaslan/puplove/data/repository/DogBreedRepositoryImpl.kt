package com.erkaslan.puplove.data.repository

import com.erkaslan.puplove.data.db.DogEntityDao
import com.erkaslan.puplove.data.models.DogBreedListResponse
import com.erkaslan.puplove.data.models.DogBreedPictureListResponse
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.data.services.DogBreedService
import com.erkaslan.puplove.util.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class DogBreedRepositoryImpl @Inject constructor(private val dogEntityDao: DogEntityDao, private val dogBreedService: DogBreedService) : DogBreedRepository {

    companion object {
        private const val MESSAGE = "message"
    }

    override suspend fun getAllBreeds(): Result<List<String>> {
        return withContext(Dispatchers.IO) {
            when(val response = handleApi { dogBreedService.getAllBreeds() }) {
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
        }
    }

    override suspend fun getBreedPictures(breedName: String): Result<List<DogEntity>> {
        return withContext(Dispatchers.IO) {
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
        }
    }

    override suspend fun updateFavoriteStatus(entity: DogEntity): Result<Unit> = suspendCoroutine {
        CoroutineScope(Dispatchers.IO).launch {
            if (entity.favorited) {
                dogEntityDao.upsert(entity)
            } else {
                dogEntityDao.delete(entity)
            }
        }
    }

    override suspend fun getAllFavorites(): List<DogEntity> = suspendCoroutine { continuation ->
        CoroutineScope(Dispatchers.IO).launch {
            val favorites = dogEntityDao.getAllFavorites().reversed()
            continuation.resume(favorites)
        }
    }

    override suspend fun deleteFavorite(dogEntity: DogEntity) = suspendCoroutine { continuation ->
        CoroutineScope(Dispatchers.IO).launch {
            dogEntityDao.delete(dogEntity)
            continuation.resume(Unit)
        }
    }
}