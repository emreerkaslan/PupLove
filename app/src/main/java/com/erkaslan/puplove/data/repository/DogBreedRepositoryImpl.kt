package com.erkaslan.puplove.data.repository

import com.erkaslan.puplove.data.db.DogEntityDao
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.data.services.DogBreedService
import com.erkaslan.puplove.util.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
                    val list = response.data.get(MESSAGE) as? JsonObject
                    val finalBreedList = mutableListOf<String>()
                    if (list?.size() != 0) {
                        list?.keySet()?.forEach { breed ->
                            finalBreedList.add(breed.replaceFirstChar { it.uppercase() })
                        }
                    }
                    Success(finalBreedList)
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
                    val list = response.data.get(MESSAGE) as? JsonArray
                    val pictureList = mutableListOf<String>()
                    if (list?.size() != 0) {
                        list?.forEach { pictureUri -> pictureList.add(pictureUri.asString) }
                    }

                    val allFavorites = dogEntityDao.getAll()
                    val finalPictureList = pictureList.map { uri ->
                        if (uri in allFavorites.map { it.pictureUri }) allFavorites.first { it.pictureUri == uri }
                        else DogEntity(pictureUri = uri, breedName = breedName, favorited = false)
                    }
                    Success(finalPictureList)
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