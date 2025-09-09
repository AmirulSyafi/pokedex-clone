package com.amiruls.pokedex.ui.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.BY_ID)
    val sortType = _sortType.asStateFlow()

    private val _filter = MutableStateFlow(PokemonFilter.ALL)
    val filter = _filter.asStateFlow()

    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.ShowList)
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    val pokemonListStateFlow: StateFlow<List<Pokemon>> =
        combine(repository.pokemonCacheFlow, _sortType, _filter) { cacheMap, sortType, filter ->
            var list = cacheMap.values.toList()

            list = when (filter) {
                PokemonFilter.ALL -> list
                PokemonFilter.FAVORITES -> list.filter { it.isFavorite }
            }

            list = when (sortType) {
                SortType.BY_ID -> list.sortedBy { it.id }
                SortType.NAME_ASC -> list.sortedBy { it.name }
                SortType.NAME_DESC -> list.sortedByDescending { it.name }
            }
            list
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(0), emptyList())

    init {
        loadPokemonList()
    }

    fun toggleSort() {
        _sortType.value = when (_sortType.value) {
            SortType.BY_ID -> SortType.NAME_ASC
            SortType.NAME_ASC -> SortType.NAME_DESC
            SortType.NAME_DESC -> SortType.BY_ID
        }
    }

    fun setFilter(filter: PokemonFilter) {
        _filter.value = filter
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            try {
                _uiState.value = PokemonListUiState.Loading
                repository.fetchPokemonList()
                _uiState.value = PokemonListUiState.ShowList
            } catch (e: Exception) {
                _uiState.value = PokemonListUiState.Error(
                    e.message ?: "Something went wrong. Please try again."
                )
            }
        }
    }

    sealed class PokemonListUiState {
        object Loading : PokemonListUiState()
        data class Error(val message: String) : PokemonListUiState()
        object ShowList : PokemonListUiState()
    }
}
