package com.erkaslan.puplove.di

import com.erkaslan.puplove.data.db.DogEntityDao
import com.erkaslan.puplove.data.repository.DogBreedRepository
import com.erkaslan.puplove.data.repository.DogBreedRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideDogBreedRepository(dogEntityDao: DogEntityDao): DogBreedRepository {
        return DogBreedRepositoryImpl(dogEntityDao)
    }
}