package com.erkaslan.puplove.data.repository

import com.erkaslan.puplove.data.db.DogEntityDao
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.data.services.ApiClient
import com.erkaslan.puplove.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class DogBreedRepositoryImpl @Inject constructor(private val dogEntityDao: DogEntityDao,) : DogBreedRepository {

    companion object {
        private const val INVALID_RESPONSE = "Response is invalid"
    }

    override suspend fun getAllBreeds() : Result<List<String>> = suspendCoroutine { continuation ->
        ApiClient.shared.getAllBreeds { data, error ->
            error?.let {
                continuation.resume(Result.Failed(error))
            } ?: data?.let {
                continuation.resume(Result.Success(data))
            } ?: continuation.resume(Result.Failed(Throwable(message = INVALID_RESPONSE)))
        }
    }

    override suspend fun getBreedPictures(breedName: String): Result<List<DogEntity>> = suspendCoroutine { continuation ->
        ApiClient.shared.getBreedPictures(breedName) { data, error ->
            error?.let {
                continuation.resume(Result.Failed(error))
            } ?: data?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val allFavorites = dogEntityDao.getAll()
                    val finalList = data.map { uri ->
                        if (uri in allFavorites.map { it.pictureUri }) allFavorites.first { it.pictureUri == uri }
                        else DogEntity(pictureUri = uri, breedName = breedName, favorited = false)
                    }
                    continuation.resume(Result.Success(finalList))
                    cancel()
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
}