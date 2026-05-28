package com.minerva.app.domain.usecase

import com.minerva.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}
