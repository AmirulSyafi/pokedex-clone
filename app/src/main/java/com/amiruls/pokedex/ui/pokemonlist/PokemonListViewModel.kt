package com.amiruls.pokedex.ui.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = _uiState

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            _uiState.value = PokemonListUiState.Loading
            try {
                val list = repository.getPokemonList()
                if (list.isEmpty()) {
                    _uiState.value = PokemonListUiState.Empty
                } else {
                    _uiState.value = PokemonListUiState.Success(list)
                }
            } catch (e: Exception) {
                _uiState.value = PokemonListUiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    sealed class PokemonListUiState {
        object Loading : PokemonListUiState()
        data class Success(val data: List<Pokemon>) : PokemonListUiState()
        data class Error(val message: String) : PokemonListUiState()
        object Empty : PokemonListUiState()
    }

}
