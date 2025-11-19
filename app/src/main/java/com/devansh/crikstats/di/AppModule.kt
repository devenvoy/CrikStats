package com.devansh.crikstats.di

import android.content.Context
import com.devansh.crikstats.utils.DynamicFeatureManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDynamicFeatureManager(
        @ApplicationContext context: Context
    ): DynamicFeatureManager {
        return DynamicFeatureManager(context)
    }
}
