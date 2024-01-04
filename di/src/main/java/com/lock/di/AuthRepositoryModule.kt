package com.lock.di

import android.app.Application
import android.content.Context
import com.lock.data.repo.ApksRepo
import com.lock.data.repo.ApksRepoImp
import com.lock.data.repo.auth.AuthRepo
import com.lock.data.repo.auth.AuthRepoImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    abstract fun providesNewRepo(repo: ApksRepoImp): ApksRepo

    @Binds
    abstract fun providesLoginRepo(repo: AuthRepoImp): AuthRepo



}