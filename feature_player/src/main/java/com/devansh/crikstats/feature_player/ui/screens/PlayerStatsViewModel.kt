package com.devansh.crikstats.feature_player.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devansh.crikstats.data.repository.CricketRepository
import com.devansh.crikstats.feature_player.model.PlayerStatsUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AssistedFactory
interface PlayerStatsViewModelFactory {
    fun create(playerId: String): PlayerStatsViewModel
}

class PlayerStatsViewModel @AssistedInject constructor(
    private val repository: CricketRepository,
    @Assisted private val playerId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerStatsUiState())
    val uiState: StateFlow<PlayerStatsUiState> = _uiState.asStateFlow()

    init {
        loadPlayerStats()
    }

    fun loadPlayerStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val stats = repository.getPlayerStats(playerId)
            stats.onSuccess {result->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        playerStats = result,
                        error = null
                    )
                }
            }.onFailure {e->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load player statistics"
                    )
                }
            }
        }
    }
}