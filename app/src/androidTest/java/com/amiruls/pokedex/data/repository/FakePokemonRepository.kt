package com.amiruls.pokedex.data.repository

import com.amiruls.pokedex.data.model.Ability
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.remote.PokemonApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePokemonRepository( private val api: PokemonApi) : PokemonRepositoryInterface {

    private val _pokemonCacheFlow = MutableStateFlow<Map<Int, Pokemon>>(emptyMap())
    override val pokemonCacheFlow: StateFlow<Map<Int, Pokemon>> = _pokemonCacheFlow

    private val _abilityCacheFlow = MutableStateFlow<Map<Int, Ability>>(emptyMap())
    override val abilityCacheFlow: StateFlow<Map<Int, Ability>> = _abilityCacheFlow

    override suspend fun fetchPokemonList() {
        val json = api.getPokemonList()
        val results = json.getAsJsonArray("results")
        val pokemonList = results.map { element ->
            val obj = element.asJsonObject
            val id = obj["url"].asString.trimEnd('/').substringAfterLast("/").toInt()
            Pokemon(
                id = id,
                name = obj["name"].asString.replaceFirstChar { it.uppercaseChar() },
                sprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
                abilityIds = emptyList(),
                isFavorite = false
            )
        }
        _pokemonCacheFlow.value = pokemonList.associateBy { it.id }
    }

    override suspend fun fetchPokemonDetail(id: Int): Pokemon {
        val cached = _pokemonCacheFlow.value[id] ?: error("Pokemon $id not in cache")
        if (cached.abilityIds.isNotEmpty()) return cached

        val json = api.getPokemonDetail(id)
        val abilitiesArray = json.getAsJsonArray("abilities")
        val abilityIds = abilitiesArray.map {
            val obj = it.asJsonObject.getAsJsonObject("ability")
            obj["url"].asString.trimEnd('/').substringAfterLast("/").toInt()
        }

        val updated = cached.copy(abilityIds = abilityIds)
        _pokemonCacheFlow.value = _pokemonCacheFlow.value.toMutableMap().apply { this[id] = updated }
        return updated
    }

    override suspend fun fetchAbilityDetail(id: Int): Ability {
        _abilityCacheFlow.value[id]?.let { return it }

        val json = api.getAbilityDetail(id)
        val name = json["name"].asString
        val description = json.getAsJsonArray("effect_entries")
            .map { it.asJsonObject }
            .firstOrNull { it.getAsJsonObject("language")["name"].asString == "en" }
            ?.get("effect")?.asString ?: "No description"

        val ability = Ability(id, name.replaceFirstChar { it.uppercaseChar() }, description)
        _abilityCacheFlow.value = _abilityCacheFlow.value.toMutableMap().apply { this[id] = ability }
        return ability
    }

    override fun getPokemon(id: Int): Pokemon = _pokemonCacheFlow.value[id]!!
    override fun updatePokemon(pokemon: Pokemon) {
        _pokemonCacheFlow.value = _pokemonCacheFlow.value.toMutableMap().apply { put(pokemon.id, pokemon) }
    }
}
