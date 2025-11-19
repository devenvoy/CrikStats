package com.devansh.crikstats.ui.screens.home

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devansh.core.utils.InstallState
import com.devansh.crikstats.utils.DynamicFeatureManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerStatsNavigationViewModel @Inject constructor(
    private val featureManager: DynamicFeatureManager
) : ViewModel() {

    private val _installState = MutableStateFlow<InstallState?>(null)
    val installState: StateFlow<InstallState?> = _installState.asStateFlow()

    fun navigateToPlayerStats(activity: Activity, playerId: String) {
        if (featureManager.isModuleInstalled(DynamicFeatureManager.FEATURE_PLAYER_MODULE)) {
            // Module already installed, launch directly
            launchPlayerStats(activity, playerId)
        } else {
            // Install module first
            installAndLaunch(activity, playerId)
        }
    }

    private fun installAndLaunch(activity: Activity, playerId: String) {
        viewModelScope.launch {
            featureManager.installModule(DynamicFeatureManager.FEATURE_PLAYER_MODULE)
                .collect { state ->
                    _installState.update { state }
                    if (state is InstallState.Installed) {
                        launchPlayerStats(activity, playerId)
                    }
                }
        }
    }

    private fun launchPlayerStats(activity: Activity, playerId: String) {
        try {
            val intent = Intent().apply {
                setClassName(
                    activity.packageName,
                    DynamicFeatureManager.PLAYER_STATS_ACTIVITY
                )
                putExtra("PLAYER_ID", playerId)
            }
            activity.startActivity(intent)
            clearInstallState()
        } catch (e: Exception) {
            _installState.update { InstallState.Failed(-1, e.message) }
        }
    }

    fun clearInstallState() {
        _installState.update { null }
    }
}