
package com.devansh.crikstats.feature_player.model

import com.devansh.crikstats.data.model.PlayerStatsResponse

data class PlayerStatsUiState(
    val isLoading: Boolean = false,
    val playerStats: PlayerStatsResponse? = null,
    val error: String? = null
)