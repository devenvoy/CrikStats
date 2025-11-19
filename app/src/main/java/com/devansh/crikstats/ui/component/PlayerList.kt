package com.devansh.crikstats.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devansh.core.data.model.PlayerSummary
import com.devansh.core.utils.InstallState

@Composable
fun PlayerList(
    players: List<PlayerSummary>,
    onPlayerClick: (PlayerSummary) -> Unit,
    installState: InstallState?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (installState is InstallState.Downloading ||
            installState is InstallState.Installing) {
            item {
                InstallProgressCard(installState)
            }
        }

        items(players) { player ->
            PlayerCard(
                player = player,
                onClick = { onPlayerClick(player) },
                enabled = installState !is InstallState.Downloading &&
                        installState !is InstallState.Installing
            )
        }
    }
}