package com.devansh.crikstats.di

import com.devansh.crikstats.data.api.CricketApiService
import com.devansh.crikstats.data.repository.CricketRepository
import com.devansh.crikstats.data.repository.CricketRepositoryImpl
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