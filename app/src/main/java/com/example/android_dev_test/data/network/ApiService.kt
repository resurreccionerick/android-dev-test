package com.example.android_dev_test.data.network

import com.example.android_dev_test.data.model.PokemonDetailsResponse
import com.example.android_dev_test.data.model.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Fetch a list
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 10, // Default limit of 20 Pokémon
        @Query("offset") offset: Int = 0 // Pagination offset
    ): PokemonResponse

    // Fetch details of pokemon
    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String
    ): PokemonDetailsResponse
}