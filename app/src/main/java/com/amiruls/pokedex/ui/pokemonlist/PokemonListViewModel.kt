package com.amiruls.pokedex.ui.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()
    private val _sortType = MutableStateFlow(SortType.BY_ID)
    val sortType = _sortType.asStateFlow()

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            try {
                _uiState.value = PokemonListUiState.Loading
                val result = repository.getPokemonList()
                _uiState.value = PokemonListUiState.Success(applySort(result, _sortType.value))
            } catch (e: Exception) {
                _uiState.value = PokemonListUiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    fun toggleSort() {
        val nextSort = when (_sortType.value) {
            SortType.BY_ID -> SortType.NAME_ASC
            SortType.NAME_ASC -> SortType.NAME_DESC
            SortType.NAME_DESC -> SortType.BY_ID
        }
        _sortType.value = nextSort

        // Re-apply sorting on current data if available
        val currentState = _uiState.value
        if (currentState is PokemonListUiState.Success) {
            _uiState.value = currentState.copy(
                pokemons = applySort(currentState.pokemons, nextSort)
            )
        }
    }

    private fun applySort(list: List<Pokemon>, sortType: SortType): List<Pokemon> {
        return when (sortType) {
            SortType.BY_ID -> list.sortedBy { it.id }
            SortType.NAME_ASC -> list.sortedBy { it.name }
            SortType.NAME_DESC -> list.sortedByDescending { it.name }
        }
    }

    sealed class PokemonListUiState {
        object Loading : PokemonListUiState()
        data class Success(val pokemons: List<Pokemon>) : PokemonListUiState()
        data class Error(val message: String) : PokemonListUiState()
    }
}
