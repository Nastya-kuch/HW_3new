package com.example.hw_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hw_3.ui.screens.DetailScreen
import com.example.hw_3.ui.screens.ListScreen
import com.example.hw_3.ui.viewmodel.CharacterDetailViewModel
import com.example.hw_3.ui.viewmodel.CharacterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RickAndMortyApp()
                }
            }
        }
    }
}

@Composable
fun RickAndMortyApp() {
    val navController = rememberNavController()
    val listViewModel: CharacterViewModel = viewModel()
    val listUiState = listViewModel.uiState

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            ListScreen(
                uiState = listUiState,
                onSearchQueryChange = { query ->
                    listViewModel.updateSearchQuery(query)
                },
                onCharacterClick = { characterId ->
                    navController.navigate("detail/$characterId")
                },
                onRetry = {
                    listViewModel.retry()
                },
                onLoadNextPage = {
                    listViewModel.loadNextPage()
                }
            )
        }

        composable(
            route = "detail/{characterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0


            val detailViewModel: CharacterDetailViewModel = remember(characterId) {
                CharacterDetailViewModel(characterId = characterId)
            }

            val detailUiState = detailViewModel.uiState

            DetailScreen(
                uiState = detailUiState,
                onRetry = { detailViewModel.retry() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}