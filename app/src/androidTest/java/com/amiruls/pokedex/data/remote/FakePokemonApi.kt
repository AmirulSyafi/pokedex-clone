package com.amiruls.pokedex.data.remote

import android.content.Context
import androidx.annotation.RawRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.amiruls.pokedex.test.R
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader

class FakePokemonApi () : PokemonApi {
    private fun readJsonFromAssets(fileName: String): JsonObject {
        InstrumentationRegistry.getInstrumentation().context.resources.assets
            .open(fileName).bufferedReader().use { reader ->
            val jsonReader = JsonReader(reader)
            return JsonParser.parseReader(jsonReader).asJsonObject
        }
    }

    override suspend fun getPokemonList(): JsonObject {
        return readJsonFromAssets("pokemon_list.json")
    }

    override suspend fun getPokemonDetail(id: Int): JsonObject {
        return readJsonFromAssets("pokemon_detail_1.json")
    }

    override suspend fun getAbilityDetail(id: Int): JsonObject {
        return if (id == 65)
            readJsonFromAssets("ability_detail_65.json")
        else
            readJsonFromAssets("ability_detail_34.json")
    }
}
