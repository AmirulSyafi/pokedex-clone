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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amiruls.pokedex.R
import com.amiruls.pokedex.ui.pokemonlist.PokemonListViewModel.PokemonListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel(),
    onPokemonClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pokemonList by viewModel.pokemonListStateFlow.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.toggleSort() }) {
                        when (sortType) {
                            SortType.BY_ID -> Icon(
                                Icons.Default.List,
                                contentDescription = "Sort by ID"
                            )

                            SortType.NAME_ASC -> Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "Sort A-Z"
                            )

                            SortType.NAME_DESC -> Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Sort Z-A"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is PokemonListUiState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is PokemonListUiState.Error -> {
                    val message = (uiState as PokemonListUiState.Error).message
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadPokemonList() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is PokemonListUiState.ShowList -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        FilterChip(
                            selected = filter == PokemonFilter.ALL,
                            onClick = { viewModel.setFilter(PokemonFilter.ALL) },
                            label = { Text("All") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = filter == PokemonFilter.FAVORITES,
                            onClick = { viewModel.setFilter(PokemonFilter.FAVORITES) },
                            label = { Text("Favorites") }
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = pokemonList,
                            key = { pokemon -> pokemon.id }
                        ) { pokemon ->
                            PokemonListItem(
                                name = pokemon.name,
                                spriteUrl = pokemon.sprite,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPokemonClick(pokemon.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}