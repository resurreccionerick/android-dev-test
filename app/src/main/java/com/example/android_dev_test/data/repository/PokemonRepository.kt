package com.example.android_dev_test.data.repository

import com.example.android_dev_test.data.model.PokemonDetailsResponse
import com.example.android_dev_test.data.model.PokemonResponse

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): PokemonResponse
    suspend fun getPokemonDetails(name: String): PokemonDetailsResponse
}