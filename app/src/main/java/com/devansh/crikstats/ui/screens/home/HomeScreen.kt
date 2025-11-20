package com.devansh.crikstats.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.devansh.crikstats.utils.InstallState
import com.devansh.crikstats.ui.component.ErrorMessage
import com.devansh.crikstats.ui.component.PlayerList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel = hiltViewModel(),
    navViewModel: PlayerStatsNavigationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: return
    val uiState by viewModel.uiState.collectAsState()
    val installState by navViewModel.installState.collectAsState()

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    ErrorMessage(
                        error = uiState.error!!,
                        onRetry = { viewModel.loadPlayers() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.players.isNotEmpty() -> {
                    PlayerList(
                        players = uiState.players,
                        onPlayerClick = { player ->
                            navViewModel.navigateToPlayerStats(activity, player.id)
                        },
                        installState = installState
                    )
                }
            }
        }
    }
}







