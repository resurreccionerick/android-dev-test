package com.example.android_dev_test.data.model

data class PokemonDetailsResponse(
    val id: Int, // Pokémon ID
    val name: String, // Pokémon name
    val height: Int, // Pokémon height
    val weight: Int, // Pokémon weight
    val sprites: Sprites, // Pokémon sprites (images)
    val types: List<Type> // Pokémon types
)

data class Sprites(
    val front_default: String? // Default front image URL
)

data class Type(
    val slot: Int, // Type slot
    val type: TypeDetail // Type details
)

data class TypeDetail(
    val name: String // Type name (e.g., "grass", "fire")
)
