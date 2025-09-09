package com.amiruls.pokedex.ui.pokemondetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amiruls.pokedex.ui.pokemondetail.PokemonDetailViewModel.PokemonDetailUiState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PokemonDetailScreen(
    viewModel: PokemonDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val pokemon by viewModel.pokemon.collectAsState()
    val abilities by viewModel.abilities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemon.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        if (pokemon.isFavorite) {
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = "Unfavorite",
                                tint = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite"
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
            when (state) {
                is PokemonDetailUiState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is PokemonDetailUiState.Error -> {
                    val message = (state as PokemonDetailUiState.Error).message
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadAbilities() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is PokemonDetailUiState.ShowAbilities -> {
                    GlideImage(
                        model = pokemon.sprite,
                        contentDescription = pokemon.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = abilities,
                            key = { ability -> ability.id }
                        ) { ability ->
                            PokemonDetailItem(
                                name = ability.name,
                                description = ability.description,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}