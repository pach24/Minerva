package com.minerva.app.domain.usecase

import com.minerva.app.core.Result
import com.minerva.app.domain.model.Profile
import com.minerva.app.domain.repository.ProfileRepository
import javax.inject.Inject

class RefreshProfileUseCase @Inject constructor(private val repo: ProfileRepository) {
    suspend operator fun invoke(): Result<Profile?> = repo.refresh()
}
