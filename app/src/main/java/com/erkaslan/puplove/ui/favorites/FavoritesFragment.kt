package com.erkaslan.puplove.ui.favorites

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.erkaslan.puplove.R
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.databinding.FragmentFavoritesBinding
import com.erkaslan.puplove.ui.detail.DogBreedPictureListAdapter
import com.erkaslan.puplove.ui.detail.FavoriteListener
import com.erkaslan.puplove.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment(), FavoriteListener {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FavoritesViewModel>()

    companion object {
        private const val SCROLL_DELAY = 200L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.filterName = Constants.ALL
        binding.rvFavoritesPictures.adapter = DogBreedPictureListAdapter(this).apply { showDescription = true }
        binding.ivFavoritesBack.setOnClickListener { findNavController().popBackStack() }
        binding.clFilter.setOnClickListener {
            val options = mutableListOf(Constants.ALL)
            val regularList = viewModel.viewState.value.allFavoritesList?.map { it.breedName?.replaceFirstChar { it.uppercase() } }?.distinct()
            regularList?.forEach { it?.let { options.add(it) } }
            showFilterDialog(requireContext(), options.toTypedArray())
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    it.filteredList?.let { list ->
                        (binding.rvFavoritesPictures.adapter as DogBreedPictureListAdapter).submitList(list)
                    }
                    it.currentFilter.let {
                        binding.filterName = it
                    }
                    it.uiEventList?.firstOrNull()?.let {
                        when (it) {
                            is UiEvent.ScrollBeginningEvent -> {
                                delay(SCROLL_DELAY)
                                binding.rvFavoritesPictures.layoutManager?.scrollToPosition(0)
                            }
                        }
                        viewModel.consumeUiEvent()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFilterDialog(context: Context, options: Array<String>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.filter_favorites))
        builder.setItems(options) { _, which ->
            viewModel.filter(options[which])
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onFavoriteClick(dogEntity: DogEntity) { viewModel.unfavorite(dogEntity) }
}