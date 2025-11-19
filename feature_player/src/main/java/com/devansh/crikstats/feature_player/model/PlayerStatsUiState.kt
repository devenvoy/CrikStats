
package com.devansh.crikstats.feature_player.model

import com.devansh.core.data.model.PlayerStatsResponse

data class PlayerStatsUiState(
    val isLoading: Boolean = false,
    val playerStats: PlayerStatsResponse? = null,
    val error: String? = null
)