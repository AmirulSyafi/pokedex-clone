package com.amiruls.pokedex.data.repository

import com.amiruls.pokedex.data.model.Ability
import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.remote.PokemonApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokemonApi
) {
    private val pokemonCache = mutableMapOf<Int, Pokemon>()

    private val abilityCache = mutableMapOf<Int, Ability>()
    suspend fun getPokemonList(): List<Pokemon> {
        if (pokemonCache.isNotEmpty()) return pokemonCache.values.toList()

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
                //for this coding challenge we will use static sprite
                sprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
                //empty abilityIds indicate not fetched yet
                abilityIds = emptyList(),
                isFavorite = false
            )
        }

        pokemonList.forEach { pokemonCache[it.id] = it }
        return pokemonCache.values.toList()
    }

    suspend fun getPokemonDetail(id: Int): Pokemon {

        // pokemonCache[id] is guaranteed non-null here because the Pokémon
        // is always fetched and inserted into the cache before this method is called.
        val cached = pokemonCache[id]!!

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

        //copy the cached Pokemon, only abilityIds updated
        val updated = cached.copy(
            abilityIds = abilityIds
        )

        pokemonCache[id] = updated

        return updated
    }

    suspend fun getAbilityDetail(id: Int): Ability {
        // Return from cache if exists
        abilityCache[id]?.let { return it }

        // Fetch detail from API (this should be ability, not pokemon)
        val json = api.getAbilityDetail(id)

        val name = json.get("name").asString

        // Parse effect_entries array
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

        abilityCache[id] = ability

        return ability
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast("/").toInt()
    }

    // Pokémon already in cache, just return
    fun getPokemon(id: Int): Pokemon = pokemonCache[id]!!
    fun updatePokemon(updated: Pokemon) {
        pokemonCache[updated.id] = updated
    }

}
