package com.benmohammad.coroutinesflow.domain.usecase

import com.benmohammad.coroutinesflow.domain.repository.UserRepository

class RefreshGetUsersUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.refresh()
}