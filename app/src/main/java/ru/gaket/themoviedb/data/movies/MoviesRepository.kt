package ru.gaket.themoviedb.data.movies

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import ru.gaket.themoviedb.data.movies.local.MovieEntity
import ru.gaket.themoviedb.domain.auth.User
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.movies.models.MovieWithReviews
import ru.gaket.themoviedb.domain.movies.models.SearchMovieWithMyReview
import ru.gaket.themoviedb.domain.review.models.ReviewDraft
import ru.gaket.themoviedb.util.Result
import ru.gaket.themoviedb.util.VoidResult
import ru.gaket.themoviedb.util.toSuccessOrErrorList

/**
 * Repository providing data about [MovieEntity]
 */
interface MoviesRepository {

    suspend fun searchMoviesWithReviews(query: String): Result<List<SearchMovieWithMyReview>, Throwable>

    suspend fun getMovieDetails(id: MovieId): Result<Movie, Throwable>

    fun observeMovieDetailsWithReviews(id: MovieId): Flow<Result<MovieWithReviews, Throwable>>

    suspend fun addReview(
        draft: ReviewDraft,
        authorId: User.Id,
        authorEmail: User.Email,
    ): VoidResult<Throwable>

    suspend fun getReviewsForUser(userId: User.Id)

    suspend fun deleteUserReviews()
}

suspend fun MoviesRepository.getMovieDetailsList(ids: Set<MovieId>): Result<List<Movie>, List<Throwable>> =
    coroutineScope {
        val asyncCalls = ids.map { singleId ->
            async { getMovieDetails(singleId) }
        }

        asyncCalls.awaitAll()
            .toSuccessOrErrorList()
    }