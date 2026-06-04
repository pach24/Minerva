package com.minerva.app.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minerva.app.presentation.login.LoginScreen
import com.minerva.app.presentation.main.MainScreen
import com.minerva.app.presentation.splash.SplashScreen

@Composable
fun MinervaNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(
            route = Routes.SPLASH,
            exitTransition = { ExitTransition.None }
        ) {
            SplashScreen(
                onFinished = {
                    navController.navigate(startDestination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.LOGIN,
            enterTransition = { EnterTransition.None },
            exitTransition = { fadeOut(tween(250)) }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.MAIN,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) }
        ) {
            MainScreen(
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
