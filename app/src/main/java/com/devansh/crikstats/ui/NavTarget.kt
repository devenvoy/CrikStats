package com.devansh.crikstats.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class NavTarget {

    @Serializable
    data object CrikStatusApp : NavTarget()

    @Serializable
    data object HomeScreen : NavTarget()
}