package com.erkaslan.puplove.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkaslan.puplove.data.services.ApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private var _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        getBreedList()
    }

    private fun getBreedList() {
        viewModelScope.launch {
            ApiClient.shared.getAllBreeds { breedList, _ ->
                _viewState.update { it.copy(dogBreedList = breedList ?: listOf()) }
            }
        }
    }
}

data class HomeViewState(val dogBreedList: List<String>? = null)