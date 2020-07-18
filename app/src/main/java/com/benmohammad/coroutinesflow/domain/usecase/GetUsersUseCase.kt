package com.benmohammad.coroutinesflow.domain.usecase

import com.benmohammad.coroutinesflow.domain.repository.UserRepository


class GetUsersUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getUsers()
}