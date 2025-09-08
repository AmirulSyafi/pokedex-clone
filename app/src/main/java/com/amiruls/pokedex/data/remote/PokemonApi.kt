package com.amiruls.pokedex.data.remote

import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.model.Ability
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

// API service
interface PokemonApi {
    @GET("pokemon?limit=1302")
    suspend fun getPokemonList(): JsonObject

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): Ability
}
