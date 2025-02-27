package com.example.android_dev_test.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_dev_test.databinding.FragmentPokemonListBinding
import com.example.android_dev_test.ui.PokemonListState
import com.example.android_dev_test.ui.adapter.PokemonListAdapter
import com.example.android_dev_test.ui.viewmodel.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PokemonViewModel by viewModels()
    private val adapter = PokemonListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        onClickListeners()
        observeData()

        // Load the Pokémon list
        viewModel.loadPokemonList()
    }

    private fun observeData() {
        // Observe the Pokémon list from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pokemonList.collect { state ->
                    when (state) {
                        is PokemonListState.Loading -> showLoading()
                        is PokemonListState.Success -> {
                            adapter.submitList(state.data.results)
                            hideLoading()
                        }

                        is PokemonListState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun onClickListeners() {
        // Handle search queries
        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // Let onQueryTextChange handle the search
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.searchPokemon(newText) // Filter the list
                } else {
                    viewModel.searchPokemon("") // Clear the filter (no need to reload the full list)
                }
                return true
            }
        })

        // Handle the clear button in the SearchView
        binding.etSearch.setOnCloseListener {
            viewModel.searchPokemon("")
            false
        }


        //for pagination
        binding.rvPokemon.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1) {
                    viewModel.loadPokemonList()
                }
            }
        })

    }

    private fun setupAdapter() {
        // Initialize RecyclerView and Adapter
        binding.rvPokemon.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPokemon.adapter = adapter

        // Handle item clicks
        adapter.onItemClick = { pokemon ->
            val action =
                PokemonListFragmentDirections.actionPokemonListFragmentToPokemonDetailFragment(
                    pokemon.name
                )
            findNavController().navigate(action)
        }
    }

    private fun showLoading() {
        binding.rvLoading.root.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.rvLoading.root.visibility = View.GONE
    }

    private fun showError(message: String) {
        hideLoading()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}