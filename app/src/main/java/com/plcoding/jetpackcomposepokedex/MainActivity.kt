package com.plcoding.jetpackcomposepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.jetpackcomposepokedex.pokemondetail.PokemonDetailScreen
import com.plcoding.jetpackcomposepokedex.pokemonlist.PokemonListScreen
import com.plcoding.jetpackcomposepokedex.pokemonquiz.PokemonQuizScreen
import com.plcoding.jetpackcomposepokedex.pokemonquiz.PokemonToGenerationQuiz
import com.plcoding.jetpackcomposepokedex.ui.theme.JetpackComposePokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePokedexTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "pokemon_list_screen",
                        Modifier.padding(innerPadding)
                    ) {
                        composable("pokemon_list_screen") {
                            PokemonListScreen(navController = navController)
                        }
                        composable(
                            "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                            arguments = listOf(
                                navArgument("dominantColor") {
                                    type = NavType.IntType
                                },
                                navArgument("pokemonName") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val dominantColor = remember {
                                val color = it.arguments?.getInt("dominantColor")
                                color?.let { Color(it) } ?: Color.White
                            }
                            val pokemonName = remember {
                                it.arguments?.getString("pokemonName")
                            }
                            PokemonDetailScreen(
                                dominantColor = dominantColor,
                                pokemonName = pokemonName?.lowercase() ?: "",
                                navController = navController
                            )
                        }
                        composable("pokemon_quiz_screen") {
                            PokemonQuizScreen(navController = navController)
                        }
                        composable("pokemon_to_generation_quiz") {
                            PokemonToGenerationQuiz(navController = navController)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        BottomNavigation {
            BottomNavigationItem(
                label = { Text("Pokedex") },
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                selected = currentRoute(navController) == "pokemon_list_screen",
                onClick = {
                    navController.navigate("pokemon_list_screen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomNavigationItem(
                label = { Text("Quiz") },
                icon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                selected = currentRoute(navController) == "pokemon_quiz_screen",
                onClick = {
                    navController.navigate("pokemon_quiz_screen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

    private fun currentRoute(navController: NavController): String? {
        return navController.currentBackStackEntry?.destination?.route
    }
}