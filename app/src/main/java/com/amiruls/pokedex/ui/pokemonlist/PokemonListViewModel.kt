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
    private val _filter = MutableStateFlow(PokemonFilter.ALL)
    val filter: StateFlow<PokemonFilter> = _filter
    private var allPokemons = emptyList<Pokemon>()

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            try {
                _uiState.value = PokemonListUiState.Loading
                allPokemons = repository.getPokemonList()
                applySortAndFilter()
            } catch (e: Exception) {
                _uiState.value = PokemonListUiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    fun toggleSort() {
        _sortType.value = when (_sortType.value) {
            SortType.BY_ID -> SortType.NAME_ASC
            SortType.NAME_ASC -> SortType.NAME_DESC
            SortType.NAME_DESC -> SortType.BY_ID
        }
        applySortAndFilter()
    }

    fun setFilter(filter: PokemonFilter) {
        _filter.value = filter
        applySortAndFilter()
    }

    private fun applySortAndFilter() {
        var list = allPokemons
        // Apply filter
        list = when (_filter.value) {
            PokemonFilter.ALL -> list
            PokemonFilter.FAVORITES -> list.filter { it.isFavorite }
        }
        // Apply sort
        list = when (_sortType.value) {
            SortType.BY_ID -> list.sortedBy { it.id }
            SortType.NAME_ASC -> list.sortedBy { it.name }
            SortType.NAME_DESC -> list.sortedByDescending { it.name }
        }
        _uiState.value = PokemonListUiState.Success(list)
    }

    sealed class PokemonListUiState {
        object Loading : PokemonListUiState()
        data class Success(val pokemons: List<Pokemon>) : PokemonListUiState()
        data class Error(val message: String) : PokemonListUiState()
    }
}
