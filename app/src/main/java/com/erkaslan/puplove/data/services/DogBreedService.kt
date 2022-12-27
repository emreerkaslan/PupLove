package com.erkaslan.puplove.data.services

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface DogBreedService {

    @GET("breeds/list/all")
    suspend fun getAllBreeds() : Response<JsonObject>

    @GET("breed/{breedName}/images")
    suspend fun getBreedPictures(@Path("breedName") breedName: String) : Response<JsonObject>
}