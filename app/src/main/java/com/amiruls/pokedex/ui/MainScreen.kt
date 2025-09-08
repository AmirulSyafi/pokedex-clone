package com.amiruls.pokedex.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amiruls.pokedex.R
import com.amiruls.pokedex.ui.pokemonlist.PokemonListScreen
import com.amiruls.pokedex.ui.theme.AppTheme

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

