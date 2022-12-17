package com.erkaslan.puplove.util

sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Failed<T>(val exception: Throwable?) : Result<T>()
}