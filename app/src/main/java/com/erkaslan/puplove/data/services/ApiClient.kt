package com.erkaslan.puplove.data.services

import com.erkaslan.puplove.util.Constants
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiClient {
    private var baseUrl = Constants.DOG_BREED_API_BASE_URL
    private var dogBreedService: DogBreedService? = null

    init {
        initializeRetrofit()
    }

    private fun initializeRetrofit() {
        val httpClientBuilder = OkHttpClient.Builder()

        val gson = GsonBuilder().create()

        val retrofit = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClientBuilder.build())
            .build()

        dogBreedService = retrofit.create(DogBreedService::class.java)
    }

    companion object {
        private const val UNSUCCESSFUL_REQUEST = "Request is unsuccessful"
        private const val MESSAGE = "message"
        val shared = ApiClient()

        private fun <T> enqueueRequest(
            call: Call<T>?,
            completion: (T?, Throwable?) -> Unit
        ) {
            call?.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    completion(null, t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.code() in 200..299) {
                        completion(response.body(), null)
                    } else {
                        completion(null, Throwable(message = UNSUCCESSFUL_REQUEST))
                    }
                }
            })
        }
    }

    fun getAllBreeds(completion: (breedList: List<String>?, Throwable?) -> Unit) {
        enqueueRequest(dogBreedService?.getAllBreeds()) { data, error ->
            error?.let {
                completion(null, error)
            } ?: data?.let {
                val list = data.get(MESSAGE) as JsonObject
                if (list.size() != 0) {
                    val finalBreedList = mutableListOf<String>()
                    list.keySet().forEach { breed ->
                        finalBreedList.add(breed.replaceFirstChar { it.uppercase() })
                    }
                    completion(finalBreedList, null)
                }
            } ?: completion(null, null)
        }
    }

    fun getBreedPictures(
        breed: String,
        completion: (breedPictureList: List<String>?, Throwable?) -> Unit
    ) {
        enqueueRequest(dogBreedService?.getBreedPictures(breed)) { data, error ->
            error?.let {
                completion(null, error)
            } ?: data?.let {
                val list = data.get(MESSAGE) as? JsonArray
                if (list != null && list.size() != 0) {
                    val finalPictureList = mutableListOf<String>()
                    list.forEach { pictureUri -> finalPictureList.add(pictureUri.asString) }
                    completion(finalPictureList, null)
                }
            } ?: completion(null, null)
        }
    }
}