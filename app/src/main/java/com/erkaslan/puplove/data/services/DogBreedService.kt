package com.erkaslan.puplove.data.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface DogBreedService {

    @GET("breeds/list/all")
    fun getAllBreeds() : Call<JsonObject>

    @GET("breed/{breedName}/images")
    fun getBreedPictures(@Path("breedName") breedName: String) : Call<JsonObject>
}