package com.erkaslan.puplove.util

import com.erkaslan.puplove.data.models.DogEntity

fun List<String>.mergeWithFavorites(breedName: String, favoriteList: List<DogEntity>): List<DogEntity> {
    return this.map { uri ->
        if (uri in favoriteList.map { it.pictureUri }) favoriteList.first { it.pictureUri == uri }
        else DogEntity(pictureUri = uri, breedName = breedName, favorited = false)
    }
}