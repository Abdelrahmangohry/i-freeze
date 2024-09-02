package com.ifreeze.di

import com.ifreeze.data.repo.ApksRepo
import com.ifreeze.data.repo.ApksRepoImp
import com.ifreeze.data.repo.auth.AuthRepo
import com.ifreeze.data.repo.auth.AuthRepoImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    abstract fun providesNewRepo(repo: ApksRepoImp): ApksRepo

    @Binds
    abstract fun providesLoginRepo(repo: AuthRepoImp): AuthRepo



}