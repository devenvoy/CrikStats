package com.devansh.crikstats.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devansh.crikstats.ui.NavTarget
import com.devansh.crikstats.ui.screens.home.HomeScreenDemo

@Composable
fun CrikStatsApp(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavTarget.HomeScreen) {
        composable<NavTarget.HomeScreen> {
            HomeScreenDemo()
        }
    }
}