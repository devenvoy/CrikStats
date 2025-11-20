package com.devansh.crikstats.feature_player.di

import android.content.Context
import com.devansh.crikstats.di.FeaturePlayerDependencies
import com.devansh.crikstats.feature_player.ui.screens.PlayerStatsViewModelFactory
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [FeatureViewModelModule::class],
    dependencies = [FeaturePlayerDependencies::class]
)
interface PlayerStatsComponent {

    fun viewModelFactory(): PlayerStatsViewModelFactory

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(dependencies: FeaturePlayerDependencies): Builder
        fun build(): PlayerStatsComponent
    }
}
