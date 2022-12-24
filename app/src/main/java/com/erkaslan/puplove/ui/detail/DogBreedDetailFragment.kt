package com.erkaslan.puplove.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erkaslan.puplove.R
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.databinding.FragmentDetailBinding
import com.erkaslan.puplove.ui.adapter.DogBreedPictureListAdapter
import com.erkaslan.puplove.ui.adapter.FavoriteListener
import com.erkaslan.puplove.ui.home.UiEvent
import com.erkaslan.puplove.util.Constants
import com.erkaslan.puplove.util.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DogBreedDetailFragment : Fragment(), FavoriteListener {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DogBreedDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.rvBreedPictures.adapter = DogBreedPictureListAdapter(this)
        val breedName = arguments?.getString(Constants.BREED_NAME)
        breedName?.let {
            viewModel.getPictures(it)
            binding.breedName = it.replaceFirstChar { it.uppercase() }
        }

        binding.ivDetailBack.setOnClickListener { findNavController().popBackStack() }
        binding.ivFavoriteNavigation.setOnClickListener { findNavController().navigate(R.id.action_navigation_detail_to_favorites) }

        binding.rvBreedPictures.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = binding.rvBreedPictures.layoutManager as? LinearLayoutManager

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    layoutManager?.findLastCompletelyVisibleItemPosition()?.let { viewModel.lastVisibleItemChanged(it) }
                }
            }
        })
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    it.pagedPictureList?.let { list ->
                        if (list.isEmpty()) {
                            binding.errorText = resources.getString(R.string.error_connection)
                            binding.tvDetailEmpty.visibility = View.VISIBLE
                        } else {
                            binding.tvDetailEmpty.visibility = View.GONE
                            (binding.rvBreedPictures.adapter as DogBreedPictureListAdapter).submitList(list)
                        }
                    }
                    it.uiEventList?.firstOrNull()?.let {
                        when (it) {
                            is UiEvent.ShowError -> {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
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

    override fun onFavoriteClick(dogEntity: DogEntity) {
        CoroutineScope(Dispatchers.Default).launch {
            if (dogEntity.favorited) {
                handleFavoriteStatusChange(FileUtils.deleteImageFile(dogEntity.filePath), dogEntity)
            } else {
                dogEntity.pictureUri.let { uri ->
                    FileUtils.createImageFile(requireContext(), uri) { success, filePath ->
                        handleFavoriteStatusChange(success, dogEntity, filePath)
                    }
                }
            }
            cancel()
        }
    }

    private fun handleFavoriteStatusChange(success: Boolean, dogEntity: DogEntity, filePath: String? = null) {
        if (!success) showErrorMessage(dogEntity.favorited)
        viewModel.changeFavoriteStatus(dogEntity.pictureUri, filePath)
    }

    private fun showErrorMessage(favoriteStatus: Boolean) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), resources.getString(if (favoriteStatus) R.string.error_delete else R.string.error_create), Toast.LENGTH_SHORT).show()
        }
    }
}