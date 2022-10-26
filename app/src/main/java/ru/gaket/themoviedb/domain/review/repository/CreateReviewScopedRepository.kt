package ru.gaket.themoviedb.domain.review.repository

import kotlinx.coroutines.flow.Flow
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.review.models.CreateReviewState
import ru.gaket.themoviedb.domain.review.models.Rating
import ru.gaket.themoviedb.domain.review.models.ReviewDraft
import ru.gaket.themoviedb.util.Result

interface CreateReviewScopedRepository {

    val movieId: MovieId

    fun observeState(): Flow<CreateReviewState>

    fun setWhatLike(whatLiked: String)
    fun setWhatDidNotLike(whatDidNotLike: String)
    fun setRating(rating: Rating)

    fun toPreviousStep()
    fun markAsFinished()

    fun buildDraft(): Result<ReviewDraft, Throwable>
}