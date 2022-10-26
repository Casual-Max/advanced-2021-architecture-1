package ru.gaket.themoviedb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.gaket.themoviedb.core.data.HeapItemStore
import ru.gaket.themoviedb.data.review.local.MyReviewsLocalDataSource
import ru.gaket.themoviedb.data.review.local.MyReviewsLocalDataSourceImpl
import ru.gaket.themoviedb.data.review.remote.ReviewsRemoteDataSource
import ru.gaket.themoviedb.data.review.remote.ReviewsRemoteDataSourceImpl
import ru.gaket.themoviedb.domain.review.models.CreateReviewState
import ru.gaket.themoviedb.domain.review.store.ItemStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ReviewBindingModule {

    @Singleton
    @Binds
    fun bindMyReviewsLocalDataSource(
        impl: MyReviewsLocalDataSourceImpl,
    ): MyReviewsLocalDataSource

    @Singleton
    @Binds
    fun bindReviewsRemoteDataSource(
        impl: ReviewsRemoteDataSourceImpl,
    ): ReviewsRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object ReviewModule {

    @Provides
    fun provideCreateReviewStateStore(): ItemStore<CreateReviewState> = HeapItemStore()
}