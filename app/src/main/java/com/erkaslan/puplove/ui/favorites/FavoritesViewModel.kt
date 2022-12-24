package com.erkaslan.puplove.ui.favorites

import androidx.lifecycle.ViewModel
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.data.repository.DogBreedRepository
import com.erkaslan.puplove.ui.home.UiEvent
import com.erkaslan.puplove.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val dogBreedRepository: DogBreedRepository) : ViewModel() {
    private var _viewState = MutableStateFlow(DetailViewState())
    val viewState: StateFlow<DetailViewState> = _viewState

    init {
        getFavorites()
    }

    private fun getFavorites() {
        CoroutineScope(Dispatchers.Default).launch {
            val allFavorites = dogBreedRepository.getAllFavorites()
            _viewState.update {
                it.copy(
                    allFavoritesList = allFavorites,
                    filteredList = allFavorites
                )
            }
            sendUiEvent(UiEvent.ScrollBeginningEvent)
        }
    }

    fun unfavorite(dogEntity: DogEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dogBreedRepository.deleteFavorite(dogEntity)

            val newList = viewState.value.filteredList?.toMutableList()
            newList?.remove(dogEntity)
            val newAllList = viewState.value.allFavoritesList?.toMutableList()
            newAllList?.remove(dogEntity)

            _viewState.update { it.copy(allFavoritesList = newAllList, filteredList = newList) }
            if (newList?.isEmpty() == true) filter(Constants.ALL)

            cancel()
        }
    }

    fun filter(newFilter: String) {
        val newFilterFixed = if (newFilter != Constants.ALL) newFilter.lowercase() else newFilter
        if (newFilter == Constants.ALL || viewState.value.allFavoritesList?.any { it.breedName == newFilterFixed } == true) {
            _viewState.update {
                it.copy(
                    filteredList = if (newFilter == Constants.ALL) it.allFavoritesList else it.allFavoritesList?.filter { it.breedName == newFilterFixed },
                    currentFilter = newFilter
                )
            }
            sendUiEvent(UiEvent.ScrollBeginningEvent)
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        val eventList = viewState.value.uiEventList?.toMutableList()
        eventList?.add(event)
        _viewState.update { it.copy(uiEventList = eventList) }
    }

    fun consumeUiEvent() {
        val eventList = viewState.value.uiEventList?.toMutableList()
        eventList?.removeAt(0)
        _viewState.update { it.copy(uiEventList = eventList) }
    }
}

data class DetailViewState(
    val allFavoritesList: List<DogEntity>? = null,
    val filteredList: List<DogEntity>? = null,
    val currentFilter: String = Constants.ALL,
    val uiEventList: List<UiEvent>? = listOf()
)