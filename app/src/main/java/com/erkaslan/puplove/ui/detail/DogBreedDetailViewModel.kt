package com.erkaslan.puplove.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkaslan.puplove.data.models.DogEntity
import com.erkaslan.puplove.data.repository.DogBreedRepository
import com.erkaslan.puplove.util.Result
import com.erkaslan.puplove.ui.home.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DogBreedDetailViewModel @Inject constructor(private val dogBreedRepository: DogBreedRepository,) : ViewModel() {
    private var _viewState = MutableStateFlow(DetailViewState())
    val viewState: StateFlow<DetailViewState> = _viewState

    companion object {
        private const val PAGE_BUFFER = 5
        private const val PAGE_SIZE = 20
    }

    fun getPictures(breedName: String) {
        viewModelScope.launch {
            when (val response = dogBreedRepository.getBreedPictures(breedName)) {
                is Result.Success -> {
                    val finalList = response.data
                    val pagedList = finalList.take(PAGE_SIZE)
                    _viewState.update { it.copy(breedAllPictureList = finalList, pagedPictureList = pagedList, lastItemIndex = pagedList.size) }
                }
                is Result.Failed -> {
                    sendUiEvent(UiEvent.ShowError(response.exception?.message ?: ""))
                    _viewState.update { it.copy(pagedPictureList = listOf()) }
                }
            }
        }
    }

    fun changeFavoriteStatus(pictureUri: String?, absolutePath: String?) {
        viewModelScope.launch {
            val newList = viewState.value.breedAllPictureList?.toMutableList()

            val entity = newList?.firstOrNull { it.pictureUri == pictureUri }

            if (entity != null) {
                entity.favorited = !entity.favorited
                entity.filePath = absolutePath

                val newEntity = DogEntity(entity.pictureUri, entity.breedName, entity.subBreedName, entity.favorited, entity.filePath)
                newList[newList.indexOf(entity)] = newEntity

                _viewState.update { DetailViewState(breedAllPictureList = newList) }
                dogBreedRepository.updateFavoriteStatus(entity)
            }
        }
    }

    fun lastVisibleItemChanged(position: Int) {
        val currentListLastIndex = viewState.value.lastItemIndex
        if (currentListLastIndex > 0) {
            if (position >= currentListLastIndex - PAGE_BUFFER && position != currentListLastIndex) {
                val newPagedList = viewState.value.breedAllPictureList?.take(currentListLastIndex + PAGE_SIZE)
                _viewState.update { it.copy(pagedPictureList = newPagedList, lastItemIndex = newPagedList?.size ?: 0) }
            }
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
    val breedAllPictureList: List<DogEntity>? = null,
    val pagedPictureList: List<DogEntity>? = null,
    val lastItemIndex: Int = -1,
    val uiEventList: List<UiEvent>? = listOf()
)