package com.benmohammad.coroutinesflow.data.mapper

import com.benmohammad.coroutinesflow.data.remote.UserResponse
import com.benmohammad.coroutinesflow.domain.Mapper
import com.benmohammad.coroutinesflow.domain.entity.User

class UserResponseToUserDomainMapper: Mapper<UserResponse, User> {

    override fun invoke(response: UserResponse): User {
        return User(
            id = response.id,
            avatar = response.avatar,
            email = response.email,
            firstName = response.firstName,
            lastName = response.lastName
        )
    }
}