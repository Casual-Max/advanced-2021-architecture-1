package ru.gaket.themoviedb.presentation.review.whatnotliked

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.domain.review.models.CreateReviewState
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.presentation.review.ReviewFieldEvent

class WhatNotLikeViewModel @AssistedInject constructor(
    @Assisted private val createReviewScopedRepository: CreateReviewScopedRepository,
) : ViewModel() {

    private val _events = MutableSharedFlow<ReviewFieldEvent>()
    val events: LiveData<ReviewFieldEvent>
        get() = _events
            .asLiveData(viewModelScope.coroutineContext)

    val initialValue: LiveData<String> = createReviewScopedRepository.observeState()
        .filterIsInstance<CreateReviewState>()
        .map { state -> state.form.whatDidNotLike }
        .filterNotNull()
        .asLiveData(viewModelScope.coroutineContext)

    fun submitInfo(whatDidNotLike: String) {
        viewModelScope.launch {
            if (whatDidNotLike.isBlank()) {
                _events.emit(ReviewFieldEvent.EMPTY_FIELD)
            } else {
                createReviewScopedRepository.setWhatDidNotLike(whatDidNotLike)
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): WhatNotLikeViewModel
    }
}