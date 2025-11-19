package com.devansh.crikstats.feature_player.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devansh.core.data.model.PlayerStatsResponse
import com.devansh.crikstats.feature_player.R

@Composable
fun PlayerStatsContent(
    stats: PlayerStatsResponse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Player Name Card
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

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stats.role?.let {
                StatCard(
                    title = "Role",
                    value = it,
                    modifier = Modifier.weight(1f)
                )
            }
            stats.placeOfBirth?.let {
                StatCard(
                    title = "Place Of Birth",
                    value = String.format("%.2f", stats.placeOfBirth),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Detailed Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Batting Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                stats.battingStyle?.let { StatRow("Batting Style", it) }
                stats.bowlingStyle?.let { StatRow("Bowling Style", it) }
//                StatRow("Centuries", stats.centuries.toString())
//                StatRow("Half Centuries", stats.halfCenturies.toString())
//                StatRow("Strike Rate", String.format("%.2f", stats.strikeRate))
            }
        }
    }
}