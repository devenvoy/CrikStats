package com.devansh.crikstats.di

import com.devansh.core.data.api.CricketApiService
import com.devansh.core.data.repository.CricketRepository
import com.devansh.core.data.repository.CricketRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePlayerRepository(apiService: CricketApiService): CricketRepository {
        return CricketRepositoryImpl(apiService)
    }
}