package com.amiruls.pokedex.data.repository

import com.amiruls.pokedex.data.model.Ability
import com.amiruls.pokedex.data.model.Pokemon
import kotlinx.coroutines.flow.StateFlow

interface PokemonRepositoryInterface {
    val pokemonCacheFlow: StateFlow<Map<Int, Pokemon>>
    val abilityCacheFlow: StateFlow<Map<Int, Ability>>

    suspend fun fetchPokemonList()
    suspend fun fetchPokemonDetail(id: Int): Pokemon
    suspend fun fetchAbilityDetail(id: Int): Ability
    fun getPokemon(id: Int): Pokemon
    fun updatePokemon(pokemon: Pokemon)
}
