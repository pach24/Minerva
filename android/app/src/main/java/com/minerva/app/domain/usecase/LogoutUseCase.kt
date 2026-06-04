package com.minerva.app.domain.usecase

import com.minerva.app.domain.repository.AuthRepository
import com.minerva.app.domain.repository.ProfileRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke() {
        authRepository.logout()
        profileRepository.clear()
    }
}
