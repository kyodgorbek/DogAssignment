package ceo.dog.dogapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ceo.dog.dogapp.data.Resource
import ceo.dog.dogapp.data.Status
import ceo.dog.dogapp.domain.Repository
import ceo.dog.dogapp.domain.model.Breed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private val viewEventsChannel = Channel<ViewEvent>(Channel.UNLIMITED)
    val viewEvents = viewEventsChannel.receiveAsFlow()

    fun breedDescription(breed: String) {
        viewModelScope.launch {
            repo.getBreedImages(breed)
                .onStart { viewEventsChannel.offer(UIEvents.Loading) }
                .collect { res ->
                    res.proceed {
                        _state.value = _state.value.copy(imageList = it, selectedBreed = breed)
                        viewEventsChannel.offer(UIEvents.BreedDescriptionLoaded)
                    }
                }
        }
    }

    fun getAllBreed() {
        viewModelScope.launch {
            repo.getAllBreed()
                .onStart { viewEventsChannel.offer(UIEvents.Loading) }
                .collect { res ->
                    res.proceed {
                        _state.value = _state.value.copy(dogList = it)
                    }
                }
        }
    }

    sealed class UIEvents {
        object Loading : ViewEvent
        data class Error(val massage: String) : ViewEvent
        object BreedDescriptionLoaded : ViewEvent
    }

    data class State(
        val dogList: List<Breed> = emptyList(),
        val imageList: List<String> = emptyList(),
        val selectedBreed: String? = null
    )

    private inline fun <T> Resource<T>.proceed(onSuccess: (T) -> Unit) {
        when (this.status) {
            Status.SUCCESS -> onSuccess.invoke(data!!)
            Status.ERROR -> viewEventsChannel.offer(UIEvents.Error(message ?: "Some error"))
        }
    }
}

interface ViewEvent
