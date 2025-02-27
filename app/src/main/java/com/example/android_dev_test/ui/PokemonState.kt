package com.example.android_dev_test.ui

import com.example.android_dev_test.data.model.PokemonDetailsResponse
import com.example.android_dev_test.data.model.PokemonResponse

sealed class PokemonListState {
    object Loading : PokemonListState()
    data class Success(val data: PokemonResponse) : PokemonListState()
    data class Error(val message: String) : PokemonListState()
}

sealed class PokemonDetailsState {
    object Loading : PokemonDetailsState()
    data class Success(val data: PokemonDetailsResponse) : PokemonDetailsState()
    data class Error(val message: String) : PokemonDetailsState()
}