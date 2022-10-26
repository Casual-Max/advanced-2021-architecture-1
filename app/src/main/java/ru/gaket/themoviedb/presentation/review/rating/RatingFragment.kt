package ru.gaket.themoviedb.presentation.review.rating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.databinding.FragmentReviewRatingBinding
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.presentation.review.CreateReviewScopedRepositoryImpl
import ru.gaket.themoviedb.util.createAbstractViewModelFactory
import ru.gaket.themoviedb.util.showSnackbar
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class RatingFragment : Fragment(R.layout.fragment_review_rating) {

    private val binding: FragmentReviewRatingBinding by viewBinding(FragmentReviewRatingBinding::bind)

    private val createReviewScopedRepository: CreateReviewScopedRepository by viewModels<CreateReviewScopedRepositoryImpl>(
        ownerProducer = { requireParentFragment() }
    )

    @Inject
    lateinit var viewModelAssistedFactory: RatingViewModel.Factory

    private val viewModel: RatingViewModel by viewModels {
        createAbstractViewModelFactory {
            viewModelAssistedFactory.create(createReviewScopedRepository = createReviewScopedRepository)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            viewModel.submit(binding.rbRateMovie.rating.roundToInt())
        }

        viewModel.event.observe(viewLifecycleOwner, ::processReviewEvent)
        viewModel.state.observe(viewLifecycleOwner, ::processReviewState)
    }

    private fun processReviewEvent(event: RatingViewModel.Event) {
        Timber.d("Event received: $event")
        when (event) {
            RatingViewModel.Event.ERROR_UNKNOWN,
            RatingViewModel.Event.ERROR_USER_NOT_SIGNED,
            -> {
                binding.rbRateMovie.isEnabled = true
                binding.btnSubmit.isEnabled = true
                requireView().showSnackbar(R.string.review_error_unknown)
            }
            RatingViewModel.Event.ERROR_ZERO_RATING -> {
                requireView().showSnackbar(R.string.review_error_zero_rating)
            }
        }
    }

    private fun processReviewState(state: RatingViewModel.State) {
        if ((state is RatingViewModel.State.Idle) && (state.rating != null)) {
            binding.rbRateMovie.rating = state.rating.starsCount.toFloat()
        }

        val isEnabled = when (state) {
            is RatingViewModel.State.Loading -> false
            is RatingViewModel.State.Idle -> true
        }

        binding.rbRateMovie.isEnabled = isEnabled
        binding.btnSubmit.isEnabled = isEnabled
    }
}