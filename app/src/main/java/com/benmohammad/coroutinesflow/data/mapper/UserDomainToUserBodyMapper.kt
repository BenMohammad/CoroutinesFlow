package com.benmohammad.coroutinesflow.data.mapper

import com.benmohammad.coroutinesflow.data.remote.UserBody
import com.benmohammad.coroutinesflow.domain.Mapper
import com.benmohammad.coroutinesflow.domain.entity.User

class UserDomainToUserBodyMapper: Mapper<User, UserBody> {

    override fun invoke(domain: User): UserBody {
        return UserBody(
            email = domain.email,
            avatar = domain.avatar,
            firstName = domain.firstName,
            lastNAme = domain.lastName
        )
    }
}