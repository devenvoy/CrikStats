package com.devansh.crikstats.feature_player.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devansh.crikstats.data.model.PlayerInfoStats
import com.devansh.crikstats.data.model.PlayerStatsResponse
import com.devansh.crikstats.feature_player.R

@Composable
fun PlayerStatsContent(
    stats: PlayerStatsResponse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Player Info Card
        Spacer(modifier = Modifier.height(16.dp))
        PlayerInfoCard(stats)

        // Player Details
        PlayerDetailsCard(stats)

        // Stats grouped by function and match type
        stats.stats?.let { StatsSection(it) }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PlayerInfoCard(stats: PlayerStatsResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = stats.playerImg,
                contentDescription = stats.name,
                placeholder = painterResource(R.drawable.placeholder),
                modifier = Modifier.height(150.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stats.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stats.country,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlayerDetailsCard(stats: PlayerStatsResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Player Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()

            stats.role?.let { StatRow("Role", it) }
            stats.battingStyle?.let { StatRow("Batting Style", it) }
            stats.bowlingStyle?.let { StatRow("Bowling Style", it) }
            stats.dateOfBirth?.let { StatRow("Date of Birth", it) }
            stats.placeOfBirth?.let {
                if (it != "--") StatRow("Place of Birth", it)
            }
        }
    }
}

@Composable
private fun StatsSection(stats: List<PlayerInfoStats>) {
    // Group stats by fn (batting/bowling)
    val groupedStats = stats.groupBy { it.fn }

    groupedStats.forEach { (fn, fnStats) ->
        if (fn.isNotEmpty()) {
            StatsFunctionCard(
                function = fn,
                stats = fnStats
            )
        }
    }
}

@Composable
private fun StatsFunctionCard(
    function: String,
    stats: List<PlayerInfoStats>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${function.replaceFirstChar { it.uppercase() }} Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            // Group by match type
            val matchTypeStats = stats.groupBy { it.matchtype }

            matchTypeStats.forEach { (matchType, typeStats) ->
                if (matchType.isNotEmpty()) {
                    MatchTypeSection(
                        matchType = matchType,
                        stats = typeStats
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchTypeSection(
    matchType: String,
    stats: List<PlayerInfoStats>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Match type header with chip
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = matchType.uppercase(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        // Stats grid
        StatsGrid(stats)
    }
}

@Composable
private fun StatsGrid(stats: List<PlayerInfoStats>) {
    // Filter out stats with non-zero values for better display
    val relevantStats = stats.filter {
        it.value.trim() != "0" &&
                it.value.trim() != "0.0" &&
                it.value.trim() != "-/-"
    }

    if (relevantStats.isEmpty()) {
        Text(
            text = "No matches played",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(8.dp)
        )
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            relevantStats.chunked(3).forEach { rowStats ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowStats.forEach { stat ->
                        StatItem(
                            label = stat.stat.trim().uppercase(),
                            value = stat.value.trim(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if less than 3 items
                    repeat(3 - rowStats.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}