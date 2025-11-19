package com.devansh.crikstats.ui.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class NavTarget {

    @Serializable
    data object CrikStatusApp : NavTarget()

    @Serializable
    data object HomeScreen : NavTarget()
}