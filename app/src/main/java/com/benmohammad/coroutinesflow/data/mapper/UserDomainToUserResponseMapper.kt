package com.benmohammad.coroutinesflow.data.mapper

import com.benmohammad.coroutinesflow.data.remote.UserResponse
import com.benmohammad.coroutinesflow.domain.Mapper
import com.benmohammad.coroutinesflow.domain.entity.User

class UserDomainToUserResponseMapper: Mapper<User, UserResponse> {

    override fun invoke(domain: User): UserResponse {
        return UserResponse(
            id = domain.id,
            avatar = domain.avatar,
            email = domain.email,
            firstName = domain.firstName,
            lastName = domain.lastName
        )
    }
}