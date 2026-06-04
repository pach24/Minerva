package com.minerva.app.domain.usecase

import com.minerva.app.domain.model.Profile
import com.minerva.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveProfileUseCase @Inject constructor(private val repo: ProfileRepository) {
    operator fun invoke(): StateFlow<Profile?> = repo.profile
}
