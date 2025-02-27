package com.example.android_dev_test.pokemon_testing.viewmodel

import com.example.android_dev_test.data.model.PokemonDetailsResponse
import com.example.android_dev_test.data.model.PokemonResponse
import com.example.android_dev_test.data.model.PokemonResult
import com.example.android_dev_test.data.model.Sprites
import com.example.android_dev_test.data.model.Type
import com.example.android_dev_test.data.model.TypeDetail
import com.example.android_dev_test.data.repository.PokemonRepository
import com.example.android_dev_test.ui.PokemonDetailsState
import com.example.android_dev_test.ui.PokemonListState
import com.example.android_dev_test.ui.viewmodel.PokemonViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PokemonViewModelTest {

    private lateinit var viewModel: PokemonViewModel
    private val repository: PokemonRepository = mockk()
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Override the Main dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel
        viewModel = PokemonViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadPokemonList_Success() = runBlocking {
        // Mock repository response
        val mockResponse = PokemonResponse(
            count = 1,
            next = null,
            previous = null,
            results = listOf(PokemonResult("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"))
        )
        coEvery { repository.getPokemonList(10, 0) } returns mockResponse

        // Call the ViewModel method
        viewModel.loadPokemonList()

        // Verify the state
        val state = viewModel.pokemonList.value
        assert(state is PokemonListState.Success)
        assertEquals(mockResponse, (state as PokemonListState.Success).data)
    }

    @Test
    fun testLoadPokemonList_Error() = runBlocking {
        // Mock repository error
        val errorMessage = "Network error"
        coEvery { repository.getPokemonList(10, 0) } throws Exception(errorMessage)

        // Call the ViewModel method
        viewModel.loadPokemonList()

        // Verify the state
        val state = viewModel.pokemonList.value
        assert(state is PokemonListState.Error)
        assertEquals(errorMessage, (state as PokemonListState.Error).message)
    }

    @Test
    fun testLoadPokemonDetails_Success() = runBlocking {
        // Mock repository response
        val mockResponse = PokemonDetailsResponse(
            id = 4,
            name = "charmander",
            height = 6,
            weight = 85,
            sprites = Sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"),
            types = listOf(Type(4, TypeDetail("fire")))
        )
        coEvery { repository.getPokemonDetails("charmander") } returns mockResponse

        // Call the ViewModel method
        viewModel.loadPokemonDetails("charmander")

        // Verify the state
        val state = viewModel.pokemonDetails.value
        assert(state is PokemonDetailsState.Success)
        assertEquals(mockResponse, (state as PokemonDetailsState.Success).data)
    }

    @Test
    fun testLoadPokemonDetails_Error() = runBlocking {
        // Mock repository error
        val errorMessage = "not found"
        coEvery { repository.getPokemonDetails("charmander") } throws Exception(errorMessage)

        // Call the ViewModel method
        viewModel.loadPokemonDetails("charmander")

        // Verify the state
        val state = viewModel.pokemonDetails.value
        assert(state is PokemonDetailsState.Error)
        assertEquals(errorMessage, (state as PokemonDetailsState.Error).message)
    }
}