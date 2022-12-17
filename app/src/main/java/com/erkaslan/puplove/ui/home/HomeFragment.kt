package com.erkaslan.puplove.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.erkaslan.puplove.R
import com.erkaslan.puplove.databinding.FragmentHomeBinding
import com.erkaslan.puplove.ui.adapter.DogBreedListAdapter
import com.erkaslan.puplove.ui.adapter.DogBreedListener
import com.erkaslan.puplove.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), DogBreedListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.viewState.value.dogBreedList?.toMutableList().isNullOrEmpty()) viewModel.getBreedList()
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (isEnabled) requireActivity().finishAffinity()
            }
        })
    }

    private fun initViews() {
        binding.rvDogBreed.adapter = DogBreedListAdapter(this)
        binding.ivFavoriteNavigation.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_favorites)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    it.dogBreedList?.let { list ->
                        if (list.isEmpty()) {
                            binding.errorText = resources.getString(R.string.error_connection)
                            binding.tvHomeEmpty.visibility = View.VISIBLE
                        } else {
                            binding.tvHomeEmpty.visibility = View.GONE
                            (binding.rvDogBreed.adapter as DogBreedListAdapter).submitList(list)
                        }
                    }
                }
            }
        }
    }

    override fun onDogBreedClicked(dogBreedName: String) {
        val bundle = bundleOf()
        bundle.putString(Constants.BREED_NAME, dogBreedName)
        findNavController().navigate(R.id.action_navigation_home_to_detail, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}