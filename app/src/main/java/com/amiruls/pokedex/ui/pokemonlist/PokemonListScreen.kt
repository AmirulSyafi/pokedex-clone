package com.amiruls.pokedex.ui.pokemonlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Added import
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.ui.pokemonlist.PokemonListViewModel.PokemonListUiState
import com.amiruls.pokedex.ui.theme.AppTheme

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel(),
    onPokemonClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is PokemonListUiState.Loading -> {
            CircularProgressIndicator()
        }

        is PokemonListUiState.Success -> {
            val list = (state as PokemonListUiState.Success).data
            LazyColumn {
                items(list) { pokemon ->
                    PokemonListItem(
                        name = pokemon.name,
                        spriteUrl = pokemon.sprite,
                        modifier = Modifier
                            .clickable { onPokemonClick(pokemon.id) }
                    )
                }
            }
        }

        is PokemonListUiState.Empty -> {
            Text("No Pokémon found.")
        }

        is PokemonListUiState.Error -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text((state as PokemonListUiState.Error).message)
                Button(onClick = { viewModel.loadPokemonList() }) {
                    Text("Retry")
                }
            }
        }
    }
}



