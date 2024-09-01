package com.ifreeze.di

import android.content.Context
import androidx.room.Room
import com.ifreeze.data.dp.AppsDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger module that provides dependencies related to Room database.
 * This module ensures that a singleton instance of the `AppsDB` database is available
 * throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    /**
     * Provides a singleton instance of the `AppsDB` Room database.
     *
     * @param context The application context used to create the database instance.
     * @return An instance of `AppsDB` configured with the application's context.
     */
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