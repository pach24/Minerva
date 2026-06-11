package com.minerva.app.presentation.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.minerva.app.R
import com.minerva.app.presentation.home.HomeScreen
import com.minerva.app.presentation.main.components.BottomTab
import com.minerva.app.presentation.main.components.MinervaBottomBar
import com.minerva.app.presentation.navigation.Routes
import com.minerva.app.presentation.prediction.PredictionScreen
import com.minerva.app.presentation.prediction.PredictionUiState
import com.minerva.app.presentation.prediction.PredictionViewModel
import com.minerva.app.presentation.prediction.StudentDetailScreen
import com.minerva.app.presentation.profile.ProfileScreen

/**
 * Shell autenticado: barra inferior (Inicio · Evaluar · Opciones) sobre un
 * `NavHost` interno. El [PredictionViewModel] se eleva aquí (scope = entrada
 * MAIN del nav externo) para que Inicio, Evaluar y el detalle de alumno
 * compartan el mismo estado de evaluación y sobreviva al cambio de pestaña.
 *
 * Las pantallas hijas no reciben el ViewModel: se les pasa el [PredictionUiState]
 * y callbacks (state hoisting), de modo que siguen siendo previsualizables y
 * desacopladas de la implementación concreta del estado.
 */
@Composable
fun MainScreen(
    onLoggedOut: () -> Unit,
    shellViewModel: MainShellViewModel = hiltViewModel(),
) {
    val predictionViewModel: PredictionViewModel = hiltViewModel()
    val predictionState by predictionViewModel.uiState.collectAsStateWithLifecycle()
    val innerNav = rememberNavController()

    val loggedOut by shellViewModel.loggedOut.collectAsStateWithLifecycle()
    val userName by shellViewModel.userName.collectAsStateWithLifecycle()

    LaunchedEffect(loggedOut) { if (loggedOut) onLoggedOut() }

    val tabs = remember {
        listOf(
            BottomTab(Routes.HOME, "Inicio", R.drawable.ic_home),
            BottomTab(Routes.EVALUAR, "Evaluar", R.drawable.ic_sparkles),
            BottomTab(Routes.OPCIONES, "Opciones", R.drawable.ic_bars),
        )
    }
    val tabRoutes = remember(tabs) { tabs.map { it.route }.toSet() }

    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // El inset superior lo gestiona cada pantalla (statusBarsPadding); aquí
        // solo aportamos el espacio de la barra inferior para no duplicarlo.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                MinervaBottomBar(
                    tabs = tabs,
                    currentRoute = currentRoute,
                    onSelect = { tab -> innerNav.navigateToTab(tab.route) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = innerNav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(220)) },
            exitTransition = { fadeOut(tween(160)) },
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    userName = userName,
                    predictionState = predictionState,
                    onEvaluarClick = { innerNav.navigateToTab(Routes.EVALUAR) }
                )
            }
            composable(Routes.EVALUAR) {
                PredictionScreen(
                    uiState = predictionState,
                    onLoadCsv = predictionViewModel::loadCsv,
                    onPredict = predictionViewModel::predict,
                    onReset = predictionViewModel::reset,
                    onOpenDetail = { index -> innerNav.navigate(Routes.detail(index)) }
                )
            }
            composable(Routes.OPCIONES) {
                ProfileScreen()
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument(Routes.DETAIL_ARG_INDEX) { type = NavType.IntType }),
                enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
                exitTransition = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(200)) },
                popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) },
            ) { entry ->
                val index = entry.arguments?.getInt(Routes.DETAIL_ARG_INDEX) ?: 0
                val prediction = (predictionState as? PredictionUiState.Results)
                    ?.predictions?.getOrNull(index)
                StudentDetailScreen(
                    prediction = prediction,
                    onBack = { innerNav.popBackStack() }
                )
            }
        }
    }
}

/** Navegación entre pestañas preservando el estado de cada una (patrón estándar de bottom-nav). */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
