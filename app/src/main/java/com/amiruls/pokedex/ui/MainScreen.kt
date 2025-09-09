package com.amiruls.pokedex.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amiruls.pokedex.ui.pokemondetail.PokemonDetailScreen
import com.amiruls.pokedex.ui.pokemonlist.PokemonListScreen

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            PokemonListScreen(
                onPokemonClick = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            PokemonDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}

