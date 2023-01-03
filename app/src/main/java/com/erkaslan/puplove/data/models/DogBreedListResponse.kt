package com.erkaslan.puplove.data.models

data class DogBreedListResponse(
    val message: Map<String, List<String>>,
    val status: String
) {
    fun asBreedListOrNull(): List<String>? {
        return message.map { it.key.replaceFirstChar { it.uppercase() } }
    }
}