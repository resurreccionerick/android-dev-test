package com.example.android_dev_test.data.model

data class PokemonResponse(
    val count: Int, // Total number of Pokémon
    val next: String?, // URL for the next page of results
    val previous: String?, // URL for the previous page of results
    val results: List<PokemonResult> // List of Pokémon
)

data class PokemonResult(
    val name: String ,
    val url: String
)