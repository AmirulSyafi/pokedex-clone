package com.amiruls.pokedex.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amiruls.pokedex.ui.pokemonlist.PokemonListScreen

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            PokemonListScreen(
                onPokemonClick = { name ->
                    navController.navigate("detail/$name")
                }
            )
        }
        /*composable("detail/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            PokemonDetailScreen(
                pokemonName = name,
                onBack = { navController.popBackStack() }
            )
        }*/
    }
}

