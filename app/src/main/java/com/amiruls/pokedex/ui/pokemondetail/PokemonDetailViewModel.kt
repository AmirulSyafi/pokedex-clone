package com.amiruls.pokedex.ui.pokemondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amiruls.pokedex.data.model.Ability
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.repository.PokemonRepository
import com.amiruls.pokedex.ui.pokemonlist.PokemonListViewModel.PokemonListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pokemonId: Int = savedStateHandle["id"]!!

    val pokemon: StateFlow<Pokemon> = repository.pokemonCacheFlow
        .map { cache -> cache.getValue(pokemonId) } // safe because guaranteed
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = repository.pokemonCacheFlow.value.getValue(pokemonId)
        )
    val abilities: StateFlow<List<Ability>> =
        combine(pokemon, repository.abilityCacheFlow) { pokemon, abilityCache ->
            // Map abilityIds -> Ability objects (only if present in cache)
            pokemon.abilityIds.mapNotNull { abilityId -> abilityCache[abilityId] }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.ShowAbilities)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        loadAbilities()
    }

    fun loadAbilities() {
        viewModelScope.launch {
            try {

                _uiState.value = PokemonDetailUiState.Loading
                // Fetch Pokémon with abilities filled
                val updatedPokemon = repository.fetchPokemonDetail(pokemonId)

                // Fetch each ability detail
                updatedPokemon.abilityIds.forEach { abilityId ->
                    repository.fetchAbilityDetail(abilityId)
                }

                _uiState.value = PokemonDetailUiState.ShowAbilities
            } catch (e: Exception) {
                _uiState.value = PokemonDetailUiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    fun toggleFavorite() {
        val current = pokemon.value // current state from StateFlow
        val updated = current.copy(isFavorite = !current.isFavorite)
        repository.updatePokemon(updated) // this updates the cacheFlow in repo
    }

    sealed class PokemonDetailUiState {
        object Loading : PokemonDetailUiState()
        data class Error(val message: String) : PokemonDetailUiState()
        object ShowAbilities : PokemonDetailUiState()
    }
}