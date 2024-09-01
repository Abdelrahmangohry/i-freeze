package com.ifreeze.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Dagger module that provides application-wide dependencies.
 * This module is installed in the `SingletonComponent`, meaning the provided dependencies will be
 * available as singletons throughout the entire application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context as a singleton.
     *
     * @param application The Application instance from which the context is retrieved.
     * @return The application context.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context = application.applicationContext

    /**
     * Provides a `Gson` instance.
     *
     * @return A new `Gson` instance.
     */
    @Provides
    fun provideGson(): Gson = Gson()
}
