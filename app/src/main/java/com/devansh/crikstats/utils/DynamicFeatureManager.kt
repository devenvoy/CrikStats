package com.devansh.crikstats.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.devansh.core.utils.InstallState
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicFeatureManager @Inject constructor(
    private val context: Context
) {
    private val splitInstallManager: SplitInstallManager =
        SplitInstallManagerFactory.create(context)

    companion object {
        const val FEATURE_PLAYER_MODULE = "feature_player"
        const val PLAYER_STATS_ACTIVITY = "com.devansh.crikstats.feature_player.PlayerStatsActivity"
    }

    /**
     * Check if a dynamic feature module is installed
     */
    fun isModuleInstalled(moduleName: String): Boolean {
        return splitInstallManager.installedModules.contains(moduleName)
    }

    /**
     * Install a dynamic feature module with progress updates
     */
    fun installModule(moduleName: String): Flow<InstallState> = callbackFlow {
        if (isModuleInstalled(moduleName)) {
            trySend(InstallState.Installed)
            close()
            return@callbackFlow
        }

        val listener = SplitInstallStateUpdatedListener { state ->
            if (state.moduleNames().contains(moduleName)) {
                when (state.status()) {
                    SplitInstallSessionStatus.PENDING -> {
                        trySend(InstallState.Pending)
                    }

                    SplitInstallSessionStatus.DOWNLOADING -> {
                        val progress = if (state.totalBytesToDownload() > 0) {
                            (state.bytesDownloaded() * 100 / state.totalBytesToDownload()).toInt()
                        } else 0
                        trySend(InstallState.Downloading(progress))
                    }

                    SplitInstallSessionStatus.INSTALLING -> {
                        trySend(InstallState.Installing)
                    }

                    SplitInstallSessionStatus.INSTALLED -> {
                        trySend(InstallState.Installed)
                        close()
                    }

                    SplitInstallSessionStatus.FAILED -> {
                        trySend(InstallState.Failed(state.errorCode()))
                        close()
                    }

                    SplitInstallSessionStatus.CANCELED -> {
                        trySend(InstallState.Canceled)
                        close()
                    }

                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        trySend(InstallState.RequiresConfirmation(state.sessionId()))
                    }

                    else -> {
                        // Other states can be ignored
                    }
                }
            }
        }

        splitInstallManager.registerListener(listener)

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnFailureListener { exception ->
                trySend(InstallState.Failed(-1, exception.message))
                close()
            }

        awaitClose {
            splitInstallManager.unregisterListener(listener)
        }
    }

    /**
     * Uninstall a dynamic feature module
     */
    fun uninstallModule(moduleName: String) {
        if (isModuleInstalled(moduleName)) {
            splitInstallManager.deferredUninstall(listOf(moduleName))
        }
    }

    /**
     * Launch Player Stats screen with dynamic module installation
     */
    fun launchPlayerStats(
        activity: Activity,
        playerId: String,
        onInstallProgress: (InstallState) -> Unit = {}
    ) {
        if (isModuleInstalled(FEATURE_PLAYER_MODULE)) {
            startPlayerStatsActivity(activity, playerId)
        } else {
            // Module not installed, trigger installation
            Toast.makeText(
                activity,
                "Downloading player stats module...",
                Toast.LENGTH_SHORT
            ).show()

            // Note: In production, you'd collect this flow in a coroutine
            // and update UI accordingly. This is a simplified version.
        }
    }

    /**
     * Start PlayerStatsActivity using explicit intent
     */
    private fun startPlayerStatsActivity(activity: Activity, playerId: String) {
        try {
            val intent = Intent().apply {
                setClassName(
                    activity.packageName,
                    PLAYER_STATS_ACTIVITY
                )
                putExtra("PLAYER_ID", playerId)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                "Failed to open player stats: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Get all installed modules
     */
    fun getInstalledModules(): Set<String> {
        return splitInstallManager.installedModules
    }
}
