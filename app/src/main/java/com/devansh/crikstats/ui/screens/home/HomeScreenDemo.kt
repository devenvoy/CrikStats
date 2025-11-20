package com.devansh.crikstats.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devansh.crikstats.data.model.PlayerSummary
import com.devansh.crikstats.ui.component.InstallProgressCard
import com.devansh.crikstats.utils.InstallState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenDemo(
    viewModel: HomeViewModel = hiltViewModel(),
    navViewModel: PlayerStatsNavigationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: return
    val installState by navViewModel.installState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedPlayer by remember { mutableStateOf<PlayerSummary?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Handle installation errors
    LaunchedEffect(installState) {
        when (val state = installState) {
            is InstallState.Failed -> {
                Toast.makeText(
                    context,
                    "Module installation failed: ${state.message}",
                    Toast.LENGTH_LONG
                ).show()
                navViewModel.clearInstallState()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cricket Stats") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Button(onClick = { navViewModel.installModule() }) {
                Text("Download Player Stats Module")
            }

            Button(onClick = { navViewModel.uninstallModule() }) {
                Text("Uninstall Player Stats Module")
            }

            installState?.let {

                InstallProgressCard(it)

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(it is InstallState.Installed) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Player Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedPlayer?.name ?: "Select a player",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Player") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor(
                                        ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                        true
                                    )
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                if (uiState.isLoading) {
                                    DropdownMenuItem(
                                        text = { Text("Loading players...") },
                                        onClick = { }
                                    )
                                } else if (uiState.error != null) {
                                    DropdownMenuItem(
                                        text = { Text(uiState.error ?: "Error loading players") },
                                        onClick = { }
                                    )
                                } else if (uiState.players.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No players available") },
                                        onClick = { }
                                    )
                                } else {
                                    uiState.players.forEach { player ->
                                        DropdownMenuItem(
                                            text = { Text(player.name) },
                                            onClick = {
                                                selectedPlayer = player
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Navigate Button
                        Button(
                            onClick = {
                                selectedPlayer?.id?.let { playerId ->
                                    navViewModel.navigateToPlayerStats(activity, playerId)
                                } ?: run {
                                    Toast.makeText(
                                        context,
                                        "Please select a player first",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = selectedPlayer != null
                        ) {
                            Text("Navigate to Player Stats")
                        }
                    }
                }
            }
        }
    }
}