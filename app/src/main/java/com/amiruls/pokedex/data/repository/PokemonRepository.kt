package com.amiruls.pokedex.data.repository

import com.amiruls.pokedex.data.model.Ability
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.remote.PokemonApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokemonApi
) {
    // Repository
    private val _pokemonCacheFlow = MutableStateFlow<Map<Int, Pokemon>>(emptyMap())
    val pokemonCacheFlow: StateFlow<Map<Int, Pokemon>> = _pokemonCacheFlow.asStateFlow()

    private val _abilityCacheFlow = MutableStateFlow<Map<Int, Ability>>(emptyMap())
    val abilityCacheFlow: StateFlow<Map<Int, Ability>> = _abilityCacheFlow.asStateFlow()

    suspend fun fetchPokemonList() {

        _pokemonCacheFlow.value = emptyMap()

        val json = api.getPokemonList()
        val results = json.getAsJsonArray("results")

        val pokemonList = results.mapIndexed { index, element ->
            val obj = element.asJsonObject
            val id = extractIdFromUrl(obj["url"].asString)
            Pokemon(
                id = id,
                name = obj["name"].asString.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                },
                //Static default sprite for this project
                sprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
                abilityIds = emptyList(),
                isFavorite = false
            )
        }

        // Update flow cache
        _pokemonCacheFlow.value = pokemonList.associateBy { it.id }
    }

    suspend fun fetchPokemonDetail(id: Int): Pokemon {

        // At this stage cache is already populated
        val cached = _pokemonCacheFlow.value[id]!!

        // If cached and already has abilities, just return
        if (cached.abilityIds.isNotEmpty()) {
            return cached
        }

        // Fetch detail from API
        val json = api.getPokemonDetail(id)
        val abilitiesArray = json.getAsJsonArray("abilities")

        val abilityIds = abilitiesArray.map { element ->
            val obj = element.asJsonObject
            val ability = obj.getAsJsonObject("ability")
            extractIdFromUrl(ability.get("url").asString)
        }

        // Copy cached Pokémon, only abilityIds updated
        val updated = cached.copy(abilityIds = abilityIds)

        // Update cache and emit new state
        _pokemonCacheFlow.value = _pokemonCacheFlow.value.toMutableMap().apply {
            this[id] = updated
        }

        return updated
    }

    suspend fun fetchAbilityDetail(id: Int): Ability {
        // Return from cache if exists
        _abilityCacheFlow.value[id]?.let { return it }

        val json = api.getAbilityDetail(id)
        val name = json.get("name").asString

        val effectEntries = json.getAsJsonArray("effect_entries")
        val description = effectEntries
            .map { it.asJsonObject }
            .firstOrNull { entry ->
                entry.getAsJsonObject("language").get("name").asString == "en"
            }
            ?.get("effect")?.asString
            ?: "No description"

        val ability = Ability(
            id = id,
            name = name.replaceFirstChar { it.uppercaseChar() },
            description = description
        )

        _abilityCacheFlow.value = _abilityCacheFlow.value.toMutableMap().apply {
            this[id] = ability
        }

        return ability
    }


    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast("/").toInt()
    }

    // Safe to assume it exists, just read from the latest value of the flow
    fun getPokemon(id: Int): Pokemon = _pokemonCacheFlow.value[id]!!
    fun updatePokemon(pokemon: Pokemon) {
        _pokemonCacheFlow.value = _pokemonCacheFlow.value.toMutableMap().apply {
            put(pokemon.id, pokemon)
        }
    }

}
