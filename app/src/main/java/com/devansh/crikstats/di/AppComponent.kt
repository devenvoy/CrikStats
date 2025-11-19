package com.devansh.crikstats.di

import android.content.Context
import com.devansh.core.data.repository.CricketRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class])
interface AppComponent : FeaturePlayerDependencies {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}