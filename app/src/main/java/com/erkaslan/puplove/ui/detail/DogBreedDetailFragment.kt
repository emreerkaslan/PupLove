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
import com.erkaslan.puplove.MainActivity
import com.erkaslan.puplove.R
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.databinding.FragmentDetailBinding
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    it.pagedPictureList?.let { list ->
                        (binding.rvBreedPictures.adapter as DogBreedPictureListAdapter).submitList(list)
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
                    FileUtils.createImageFile(activity as MainActivity, uri) { success, filePath ->
                        handleFavoriteStatusChange(success, dogEntity, filePath)
                    }
                }
            }
            cancel()
        }
    }

    private fun handleFavoriteStatusChange(success: Boolean, dogEntity: DogEntity, filePath: String? = null) {
        //if (!success) showErrorMessage()
        viewModel.changeFavoriteStatus(dogEntity.pictureUri, filePath)
    }

    private fun showErrorMessage() {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), "Image is not saved to storage", Toast.LENGTH_SHORT).show()
        }
    }
}