package com.amiruls.pokedex.ui.pokemondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amiruls.pokedex.data.model.Ability
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pokemonId: Int = savedStateHandle["id"]!!

    // Load pokemon from cache as it guarantee to have value
    private val _pokemon = MutableStateFlow(repository.getPokemon(pokemonId))
    val pokemon: StateFlow<Pokemon> = _pokemon

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        loadAbilities()
    }

    fun loadAbilities() {
        viewModelScope.launch {
            try {
                _uiState.value = PokemonDetailUiState.Loading
                val pokemon = repository.getPokemonDetail(pokemonId)
                val abilities = pokemon.abilityIds.map { repository.getAbilityDetail(it) }
                _uiState.value = PokemonDetailUiState.Success(abilities)
            } catch (e: Exception) {
                _uiState.value = PokemonDetailUiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    fun toggleFavorite() {
        val current = _pokemon.value
        val updated = current.copy(isFavorite = !current.isFavorite)
        _pokemon.value = updated
        repository.updatePokemon(updated)
    }

    sealed class PokemonDetailUiState {
        object Loading : PokemonDetailUiState()
        data class Success(val abilities: List<Ability>) : PokemonDetailUiState()
        data class Error(val message: String) : PokemonDetailUiState()
    }
}