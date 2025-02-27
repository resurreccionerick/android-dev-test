package com.example.android_dev_test.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_dev_test.ui.PokemonDetailsState
import com.example.android_dev_test.ui.PokemonListState
import com.example.android_dev_test.data.model.PokemonResponse
import com.example.android_dev_test.data.model.PokemonResult
import com.example.android_dev_test.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<PokemonListState>(PokemonListState.Loading)
    val pokemonList: StateFlow<PokemonListState> = _pokemonList.asStateFlow()

    private val _pokemonDetails = MutableStateFlow<PokemonDetailsState>(PokemonDetailsState.Loading)
    val pokemonDetails: StateFlow<PokemonDetailsState> = _pokemonDetails.asStateFlow()

    private var fullPokemonList: List<PokemonResult> = emptyList()

    private val _searchQuery = MutableStateFlow<String?>(null)

    //for pagination
    private var currentOffset = 0
    private val limit = 20
    private var isLastPage = false

    private val searchQueryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(200) // Wait 200ms after the user stops typing
                .collect { query ->
                    _searchQuery.value = query
                    applySearchFilter()
                }
        }
    }

    fun loadPokemonList() {
        if (isLastPage) return

        viewModelScope.launch {
            _pokemonList.value = PokemonListState.Loading

            try {
                val response = repository.getPokemonList(limit, currentOffset)
                fullPokemonList = fullPokemonList + response.results

                _pokemonList.value = PokemonListState.Success(
                    PokemonResponse(
                        response.count,
                        response.next,
                        response.previous,
                        fullPokemonList
                    )
                )


                currentOffset += limit
                isLastPage = response.next == null
            } catch (e: Exception) {
                _pokemonList.value = PokemonListState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun loadPokemonDetails(name: String) {
        viewModelScope.launch {
            _pokemonDetails.value = PokemonDetailsState.Loading
            try {
                val response = repository.getPokemonDetails(name)
                _pokemonDetails.value = PokemonDetailsState.Success(response)
            } catch (e: Exception) {
                _pokemonDetails.value = PokemonDetailsState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun searchPokemon(query: String) {
        searchQueryFlow.value = query
    }

    private fun applySearchFilter() {
        val query = _searchQuery.value
        if (query.isNullOrEmpty()) {
            _pokemonList.value = PokemonListState.Success(
                PokemonResponse(
                    fullPokemonList.size,
                    null,
                    null,
                    fullPokemonList
                )
            )
        } else {
            val filteredList = fullPokemonList.filter { pokemon ->
                pokemon.name.contains(query, ignoreCase = true)
            }.take(1)

            _pokemonList.value = PokemonListState.Success(
                PokemonResponse(
                    filteredList.size,
                    null,
                    null,
                    filteredList
                )
            )
        }
    }
}