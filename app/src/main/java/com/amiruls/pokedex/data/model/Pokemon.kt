package com.amiruls.pokedex.data.model

data class Pokemon(
    val id: Int,
    val name: String,
    val sprite: String,
    val abilityIds: List<Int>
)