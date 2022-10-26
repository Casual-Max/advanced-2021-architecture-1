package ru.gaket.themoviedb.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.domain.review.models.CreateReviewState
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.util.Result
import ru.gaket.themoviedb.util.emitIfActive

class ReviewViewModel @AssistedInject constructor(
    @Assisted private val createReviewScopedRepository: CreateReviewScopedRepository,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    val state: LiveData<ReviewViewModel.State> =
        observeState()
            .asLiveData(viewModelScope.coroutineContext)

    fun toPreviousStep() = createReviewScopedRepository.toPreviousStep()

    private fun observeState(): Flow<ReviewViewModel.State> =
        combine(
            flow {
                val details = moviesRepository.getMovieDetails(createReviewScopedRepository.movieId)
                emitIfActive(details)
            },
            createReviewScopedRepository.observeState()
        ) { movieResult, state ->
            when (movieResult) {
                is Result.Success -> ReviewViewModel.State.Data(movieResult.result, state)
                is Result.Error -> ReviewViewModel.State.NoMovie
            }
        }

    sealed class State {

        object NoMovie : State()

        data class Data(
            val movie: Movie,
            val createState: CreateReviewState,
        ) : State()
    }

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): ReviewViewModel
    }
}