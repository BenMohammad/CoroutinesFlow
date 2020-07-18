package com.benmohammad.coroutinesflow.domain.repository

import com.benmohammad.coroutinesflow.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUsers(): Flow<List<User>>

    suspend fun refresh()
    
    suspend fun remove(user: User)

    suspend fun add(user: User)
}