package com.erkaslan.puplove.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erkaslan.puplove.data.models.DogEntity

@Database(entities = [DogEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogEntityDao(): DogEntityDao
}