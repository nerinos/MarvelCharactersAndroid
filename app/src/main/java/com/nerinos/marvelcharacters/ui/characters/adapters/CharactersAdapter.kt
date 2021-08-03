package com.nerinos.marvelcharacters.ui.characters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nerinos.marvelcharacters.R
import com.nerinos.marvelcharacters.data.entities.MarvelCharacter
import com.nerinos.marvelcharacters.databinding.ItemCharacterBinding


class CharactersAdapter : PagingDataAdapter<MarvelCharacter, CharactersAdapter.CharacterViewHolder>(
    CHARACTER_COMPARATOR
) {


    inner class CharacterViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(character: MarvelCharacter) {
            binding.apply {
                Glide.with(itemView)
                    .load(character.thumbnail.path + "." + character.thumbnail.extension)
                    .error(R.drawable.ic_error)
                    .into(ivPicture)

                tvCharacterName.text = character.name
                tvCharacterDescription.text = character.description
            }
        }

    }

    companion object {
        private val CHARACTER_COMPARATOR = object : DiffUtil.ItemCallback<MarvelCharacter>() {
            override fun areItemsTheSame(oldItem: MarvelCharacter, newItem: MarvelCharacter): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MarvelCharacter,
                newItem: MarvelCharacter
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding)
    }
}