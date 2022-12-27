package com.erkaslan.puplove.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkaslan.puplove.data.repository.DogBreedRepository
import com.erkaslan.puplove.util.Failed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.erkaslan.puplove.util.Success
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dogBreedRepository: DogBreedRepository) : ViewModel() {
    private var _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        getBreedList()
    }

    fun getBreedList() {
        viewModelScope.launch {
            when (val response = dogBreedRepository.getAllBreeds()) {
                is Success -> {
                    _viewState.update { it.copy(dogBreedList = response.data) }
                }
                is Failed -> {
                    _viewState.update { it.copy(dogBreedList = listOf()) }
                }
            }
        }
    }
}

data class HomeViewState(val dogBreedList: List<String>? = null)

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    object ScrollBeginningEvent : UiEvent()
}