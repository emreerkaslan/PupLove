package com.erkaslan.puplove.data.services

import com.erkaslan.puplove.data.models.DogBreedListResponse
import com.erkaslan.puplove.data.models.DogBreedPictureListResponse
import retrofit2.Response
import retrofit2.http.*

interface DogBreedService {

    @GET("breeds/list/all")
    suspend fun getAllBreeds() : Response<DogBreedListResponse>

    @GET("breed/{breedName}/images")
    suspend fun getBreedPictures(@Path("breedName") breedName: String) : Response<DogBreedPictureListResponse>
}