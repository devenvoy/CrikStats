package com.devansh.core.utils

sealed class InstallState {
    object Pending : InstallState()
    data class Downloading(val progress: Int) : InstallState()
    object Installing : InstallState()
    object Installed : InstallState()
    data class Failed(val errorCode: Int, val message: String? = null) : InstallState()
    object Canceled : InstallState()
    data class RequiresConfirmation(val sessionId: Int) : InstallState()
}