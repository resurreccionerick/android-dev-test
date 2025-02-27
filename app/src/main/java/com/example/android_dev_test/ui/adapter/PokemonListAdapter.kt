package com.example.android_dev_test.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android_dev_test.R
import com.example.android_dev_test.data.model.PokemonResult
import com.example.android_dev_test.databinding.PokemonItemBinding

class PokemonListAdapter :
    ListAdapter<PokemonResult, PokemonListAdapter.PokemonViewHolder>(DiffCallback()) {

    var onItemClick: ((PokemonResult) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = PokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)
    }

    inner class PokemonViewHolder(private val binding: PokemonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: PokemonResult) {
            binding.txtPokemonName.text = pokemon.name

            binding.imgPokemon.load(pokemon.url) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.baseline_error_24)
            }


            binding.root.setOnClickListener {
                onItemClick?.invoke(pokemon)
            }

        }
    }


    class DiffCallback : DiffUtil.ItemCallback<PokemonResult>() {
        override fun areItemsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
            return oldItem == newItem
        }
    }
}