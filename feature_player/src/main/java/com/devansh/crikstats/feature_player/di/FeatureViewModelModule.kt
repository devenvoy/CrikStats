package com.devansh.crikstats.feature_player.di

import androidx.lifecycle.ViewModel
import com.devansh.crikstats.feature_player.ui.screens.PlayerStatsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FeatureViewModelModule {

//    @Binds
//    @IntoMap
//    @ViewModelKey(PlayerStatsViewModel::class)
//    abstract fun bindPlayerStatsViewModel(viewModel: PlayerStatsViewModel): ViewModel
}