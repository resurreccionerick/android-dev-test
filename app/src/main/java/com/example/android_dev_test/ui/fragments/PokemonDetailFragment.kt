package com.example.android_dev_test.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.android_dev_test.R
import com.example.android_dev_test.data.model.PokemonDetailsResponse
import com.example.android_dev_test.databinding.FragmentPokemonDetailBinding
import com.example.android_dev_test.ui.PokemonDetailsState
import com.example.android_dev_test.ui.viewmodel.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PokemonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the Toolbar
        setupToolbar()
        observeData()

        val pokemonName = arguments?.getString("pokemonName") ?: return

        viewModel.loadPokemonDetails(pokemonName)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pokemonDetails.collect { state ->
                    when (state) {
                        is PokemonDetailsState.Loading -> showLoading()
                        is PokemonDetailsState.Success -> {
                            bindPokemonDetails(state.data)
                            binding.rvLoading.root.visibility = View.GONE
                        }

                        is PokemonDetailsState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        // Set the Toolbar as the ActionBar
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Enable the back button
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        // Handle back button clicks
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // Navigate back
        }
    }

    private fun bindPokemonDetails(pokemonResponse: PokemonDetailsResponse) {
        binding.txtPokemonName.text = pokemonResponse.name
        binding.txtPokemonHeight.text = "Height: ${pokemonResponse.height}"
        binding.txtPokemonWeight.text = "Weight: ${pokemonResponse.weight}"
        binding.imgPokemon.load(pokemonResponse.sprites.front_default) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.baseline_error_24)
        }
        binding.txtPokemonTypes.text =
            "Types: ${pokemonResponse.types.joinToString { it.type.name }}"
    }

    private fun showLoading() {
        binding.rvLoading.root.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.rvLoading.root.visibility = View.GONE
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}