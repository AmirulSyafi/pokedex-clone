package com.amiruls.pokedex.data.remote

import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.model.PokemonDetail
import retrofit2.http.GET
import retrofit2.http.Path

// API service
interface PokemonApi {
    @GET("pokemon?limit=50")
    suspend fun getPokemonList(): List<Pokemon>

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): PokemonDetail
}
