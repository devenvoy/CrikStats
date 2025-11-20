package com.devansh.crikstats.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devansh.crikstats.data.model.PlayerSummary
import com.devansh.crikstats.data.repository.CricketRepository
import com.devansh.crikstats.utils.StaticPlayerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val players: List<PlayerSummary> = emptyList(),
    val error: String? = null
)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CricketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadStaticData()
    }

    fun loadPlayers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val players = repository.getPlayers()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    players = players,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load players"
                )
            }
        }
    }

    fun loadStaticData() {
        _uiState.update {
            it.copy(
                isLoading = false, error = null, players = StaticPlayerData.players
            )
        }
    }
}