package com.example.android_dev_test.pokemon_testing.data.api

import com.example.android_dev_test.data.network.ApiService
import com.example.android_dev_test.data.network.PokemonRetrofit
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PokemonServiceTest {

    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        apiService = PokemonRetrofit.pokemonApiService
    }

    @Test
    fun testGetPokemonList() = runBlocking {
        // Fetch the list
        val response = apiService.getPokemonList(limit = 10, offset = 0)

        // check that the response is not null
        assertNotNull(response)

        // check that the list is not empty
        assertTrue(response.results.isNotEmpty())

        // log the first pokemon
        println("pokemon is: ${response.results[0].name}")
    }

    @Test
    fun testGetPokemonDetails() = runBlocking {
        // Fetch details
        val response = apiService.getPokemonDetails("charmander")

        // check that the response is not null
        assertNotNull(response)

        // check if the pokemon name is "Charmander"
        assertEquals("charmander", response.name)

        // log the details
        println("pokemon details: $response")
    }
}