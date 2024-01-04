package com.lock.di

import android.content.Context
import androidx.room.Room
import com.lock.data.dp.AppsDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    @Singleton
    fun provideRoomDB(@ApplicationContext context: Context): AppsDB {
        return Room.databaseBuilder(
            context,
            AppsDB::class.java,
            "Apps"
        ).allowMainThreadQueries().build()
    }
}