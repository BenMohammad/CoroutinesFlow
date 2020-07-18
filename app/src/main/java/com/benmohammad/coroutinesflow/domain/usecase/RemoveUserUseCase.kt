package com.benmohammad.coroutinesflow.domain.usecase

import com.benmohammad.coroutinesflow.domain.entity.User
import com.benmohammad.coroutinesflow.domain.repository.UserRepository

class RemoveUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User) = userRepository.remove(user)
}