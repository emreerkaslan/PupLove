package com.erkaslan.puplove.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.databinding.RowLayoutBreedPictureBinding

class DogBreedPictureListAdapter (private val favoriteListener: FavoriteListener) : ListAdapter<DogEntity, RecyclerView.ViewHolder>(DogEntityDiffCallBack()) {

    var showDescription: Boolean = false

    private class DogEntityDiffCallBack : DiffUtil.ItemCallback<DogEntity>() {
        override fun areItemsTheSame(oldItem: DogEntity, newItem: DogEntity): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DogEntity, newItem: DogEntity): Boolean =
            oldItem.pictureUri == newItem.pictureUri && oldItem.favorited == newItem.favorited && oldItem.breedName == newItem.breedName
                    && oldItem.subBreedName == newItem.subBreedName && oldItem.filePath == newItem.filePath
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DogBreedPictureViewHolder(
            RowLayoutBreedPictureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), favoriteListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DogBreedPictureViewHolder).bind(getItem(position))
    }

    inner class DogBreedPictureViewHolder(private val binding: RowLayoutBreedPictureBinding, private val listener: FavoriteListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dogEntity: DogEntity) {
            binding.showDescription = showDescription
            binding.dogEntity = dogEntity
            binding.breedName = dogEntity.breedName?.replaceFirstChar { it.uppercase() }
            binding.favorited = dogEntity.favorited
            binding.ivFavorite.setOnClickListener {
                binding.favorited = !dogEntity.favorited
                listener.onFavoriteClick(dogEntity)
            }
        }
    }
}

interface FavoriteListener {
    fun onFavoriteClick(dogEntity: DogEntity)
}