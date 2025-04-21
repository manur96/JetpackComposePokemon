package com.plcoding.jetpackcomposepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val route = navBackStackEntry?.destination?.route

        BottomNavigation {
            val isPokedexSelected = route == "pokemon_list_screen"
            val isQuizSelected = route == "pokemon_quiz_screen"
            BottomNavigationItem(
                label = { Text("Pokedex") },
                icon = {
                    NavIconWithIndicator(
                        iconRes = R.drawable.ic_pok_edex_icon,
                        isSelected = isPokedexSelected,
                        iconSize = 24.dp,
                        iconOffsetY = (-2).dp
                    )
                },
                selected = isPokedexSelected,
                onClick = {
                    navController.navigate("pokemon_list_screen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomNavigationItem(
                label = { Text("Quiz") },
                icon = {
                    NavIconWithIndicator(
                        iconRes = R.drawable.ic_pok_equiz,
                        isSelected = isQuizSelected,
                        iconSize = 23.dp
                    )
                },
                selected = isQuizSelected,
                onClick = {
                    navController.navigate("pokemon_quiz_screen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

    @Composable
    fun NavIconWithIndicator(
        iconRes: Int,
        isSelected: Boolean,
        iconSize: Dp,
        iconOffsetY: Dp = 0.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) Color(0xFF1B3D0A) else Color.Transparent
                    )
            )
            Spacer(Modifier.height(6.dp))
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .offset(y = iconOffsetY)
            )
        }
    }
}