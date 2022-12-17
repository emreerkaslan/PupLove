package com.erkaslan.puplove.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.puplove.databinding.RowLayoutDogBreedBinding

class DogBreedListAdapter (private val dogBreedListener: DogBreedListener) : ListAdapter<String, RecyclerView.ViewHolder>(DogBreedDiffCallBack()) {

    private class DogBreedDiffCallBack : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DogBreedViewHolder(
            RowLayoutDogBreedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), dogBreedListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DogBreedViewHolder).bind(getItem(position))
    }

    inner class DogBreedViewHolder(private val binding: RowLayoutDogBreedBinding, private val listener: DogBreedListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dogBreed: String) {
            binding.dogBreedName = dogBreed
            binding.root.setOnClickListener { listener.onDogBreedClicked(dogBreedName = dogBreed.lowercase()) }
        }
    }
}

interface DogBreedListener {
    fun onDogBreedClicked(dogBreedName: String)
}