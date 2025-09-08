package com.amiruls.pokedex.data.repository

import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.model.PokemonDetail
import com.amiruls.pokedex.data.remote.PokemonApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokemonApi
) {
    private val pokemonCache = mutableMapOf<Int, PokemonDetail>()

    suspend fun getPokemonList(): List<Pokemon> {
        return api.getPokemonList()
    }

    suspend fun getPokemonDetail(id: Int): PokemonDetail {
        // Return cached if available
        return pokemonCache[id] ?: api.getPokemonDetail(id).also {
            pokemonCache[id] = it
        }
    }
}
