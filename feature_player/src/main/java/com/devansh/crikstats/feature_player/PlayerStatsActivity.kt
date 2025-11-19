package com.devansh.crikstats.feature_player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devansh.crikstats.CrikStatsApplication
import com.devansh.crikstats.feature_player.di.DaggerPlayerStatsComponent
import com.devansh.crikstats.feature_player.ui.screens.PlayerStatsScreen
import com.devansh.crikstats.feature_player.ui.screens.PlayerStatsViewModel
import com.devansh.crikstats.feature_player.ui.screens.PlayerStatsViewModelFactory
import com.devansh.crikstats.ui.theme.CrikStatsTheme

class PlayerStatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerId = intent.getStringExtra("PLAYER_ID") ?: ""

        /*
        val dependencies = EntryPointAccessors.fromApplication(
        this.applicationContext,
        FeaturePlayerDependencies::class.java
        )

        val viewModelFactory = DaggerPlayerStatsComponent.builder()
        .context(this.applicationContext)
        .appDependencies(dependencies)
        .build()
        .viewModelFactory()


        val appDependencies = object : FeaturePlayerDependencies {
            override fun cricketRepository(): CricketRepository {
                return dependencies.cricketRepository()
            }
        }
        */

        val appDependencies = (applicationContext as CrikStatsApplication).appComponent

        val viewModelFactory = DaggerPlayerStatsComponent.builder()
            .context(applicationContext)
            .appDependencies(appDependencies)
            .build()
            .viewModelFactory()

        setContent {
            CrikStatsTheme {
                val viewModel: PlayerStatsViewModel = viewModel(
                    factory = provideFactory(viewModelFactory, playerId)
                )
                PlayerStatsScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun provideFactory(
        assistedFactory: PlayerStatsViewModelFactory,
        playerId: String
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(playerId) as T
        }
    }
}