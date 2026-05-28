package com.minerva.app.di

import com.minerva.app.data.repository.AuthRepositoryImpl
import com.minerva.app.data.repository.PredictionRepositoryImpl
import com.minerva.app.domain.repository.AuthRepository
import com.minerva.app.domain.repository.PredictionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPredictionRepository(impl: PredictionRepositoryImpl): PredictionRepository
}
