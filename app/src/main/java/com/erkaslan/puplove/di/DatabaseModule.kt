package com.erkaslan.puplove.di

import android.content.Context
import androidx.room.Room
import com.erkaslan.puplove.data.db.AppDatabase
import com.erkaslan.puplove.data.db.DogEntityDao
import com.erkaslan.puplove.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideDogEntityDao(appDatabase: AppDatabase): DogEntityDao {
        return appDatabase.dogEntityDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }
}