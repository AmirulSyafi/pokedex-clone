package com.amiruls.pokedex.data.repository

import com.amiruls.pokedex.data.model.Pokemon
import com.amiruls.pokedex.data.model.Ability
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
                abilityIds = emptyList()
            )
        }

        pokemonList.forEach { pokemonCache[it.id] = it }
        return pokemonCache.values.toList()
    }

/*    suspend fun getPokemonDetail(id: Int): Pokemon {
        // If already cached (and has abilities filled), return it
        pokemonCache[id]?.let { cached ->
            if (cached.abilityIds.isNotEmpty()) return cached
        }

        // Fetch detail from API
        val detailResponse = api.getPokemonDetail(id)
        val abilityIds = detailResponse.abilities.map {
            extractIdFromUrl(it.ability.url)
        }

        // Update cache entry with abilityIds
        val updated = pokemonCache[id]?.copy(abilityIds = abilityIds)
            ?: Pokemon(
                id = id,
                name = detailResponse.name,
                sprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
                abilityIds = abilityIds
            )

        pokemonCache[id] = updated
        return updated
    }

    suspend fun getAbilityDetail(id: Int): Ability {
        abilityCache[id]?.let { return it }

        val response = api.getAbilityDetail(id)
        val description = response.effect_entries
            .firstOrNull { it.language.name == "en" }
            ?.short_effect ?: "No description"

        val ability = Ability(
            id = id,
            name = response.name,
            description = description
        )

        abilityCache[id] = ability
        return ability
    }*/

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast("/").toInt()
    }
}
