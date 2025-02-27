package com.example.android_dev_test.data.repository

import com.example.android_dev_test.data.local.PokemonDatabase
import com.example.android_dev_test.data.local.PokemonDetailsEntity
import com.example.android_dev_test.data.local.PokemonEntity
import com.example.android_dev_test.data.model.PokemonDetailsResponse
import com.example.android_dev_test.data.model.PokemonResponse
import com.example.android_dev_test.data.network.ApiService
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: PokemonDatabase
) :
    PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): PokemonResponse {
        val response = apiService.getPokemonList(limit, offset)
        return response.copy(
            results = response.results.map { pokemon ->
                val pokemonId = pokemon.url.split("/").dropLast(1).last()
                pokemon.copy(url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png")
            }
        )
    }

    override suspend fun getPokemonDetails(name: String): PokemonDetailsResponse {
        return apiService.getPokemonDetails(name)
    }

    //  methods to get data from the database
    suspend fun getPokemonListFromDatabase(): List<PokemonEntity> {
        return database.pokemonDao().getPokemonList()
    }

    suspend fun getPokemonDetailsFromDatabase(name: String): PokemonDetailsEntity? {
        return database.pokemonDao().getPokemonDetails(name)
    }
}