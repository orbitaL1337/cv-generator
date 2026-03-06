package com.fitplannerpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fitplannerpro.data.datastore.UserSettingsStore
import com.fitplannerpro.data.local.AppDatabase
import com.fitplannerpro.data.repository.FitRepository
import com.fitplannerpro.navigation.BottomDestination
import com.fitplannerpro.ui.screens.AtlasScreen
import com.fitplannerpro.ui.screens.DashboardScreen
import com.fitplannerpro.ui.screens.DietScreen
import com.fitplannerpro.ui.screens.PlansScreen
import com.fitplannerpro.ui.screens.ProfileScreen
import com.fitplannerpro.ui.theme.FitPlannerTheme
import com.fitplannerpro.ui.viewmodel.AppViewModel
import com.fitplannerpro.ui.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = FitRepository(db)
        val settingsStore = UserSettingsStore(applicationContext)

        setContent {
            val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(repository, settingsStore))
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            FitPlannerTheme(darkTheme = state.darkMode) {
                FitPlannerApp(viewModel)
            }
        }
    }
}

@Composable
fun FitPlannerApp(viewModel: AppViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val destinations = BottomDestination.entries

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val current = navBackStackEntry?.destination?.route
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = current == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    BottomDestination.Start -> Icons.Default.Home
                                    BottomDestination.Plans -> Icons.AutoMirrored.Filled.List
                                    BottomDestination.Diet -> Icons.Default.Fastfood
                                    BottomDestination.Atlas -> Icons.Default.FitnessCenter
                                    BottomDestination.Profile -> Icons.Default.Person
                                },
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = BottomDestination.Start.route, modifier = Modifier.padding(paddingValues)) {
            composable(BottomDestination.Start.route) { DashboardScreen(state) }
            composable(BottomDestination.Plans.route) { PlansScreen(state, viewModel) }
            composable(BottomDestination.Diet.route) { DietScreen(state, viewModel) }
            composable(BottomDestination.Atlas.route) { AtlasScreen(state, viewModel) }
            composable(BottomDestination.Profile.route) { ProfileScreen(state, viewModel) }
        }
    }
}
