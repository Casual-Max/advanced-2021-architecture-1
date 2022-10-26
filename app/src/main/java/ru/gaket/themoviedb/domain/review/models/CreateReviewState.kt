package ru.gaket.themoviedb.domain.review.models

data class CreateReviewState(
    val form: CreateReviewForm,
    val step: CreateReviewStep
)