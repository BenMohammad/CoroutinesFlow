package com.benmohammad.coroutinesflow.koin

import com.benmohammad.coroutinesflow.data.UserRepositoryImpl
import com.benmohammad.coroutinesflow.data.mapper.UserDomainToUserBodyMapper
import com.benmohammad.coroutinesflow.data.mapper.UserDomainToUserResponseMapper
import com.benmohammad.coroutinesflow.data.mapper.UserResponseToUserDomainMapper
import com.benmohammad.coroutinesflow.domain.dispatchers.CoroutineDispatchers
import com.benmohammad.coroutinesflow.domain.dispatchers.CoroutineDispatchersImpl
import com.benmohammad.coroutinesflow.domain.repository.UserRepository
import com.benmohammad.coroutinesflow.domain.usecase.AddUserUseCase
import com.benmohammad.coroutinesflow.domain.usecase.GetUsersUseCase
import com.benmohammad.coroutinesflow.domain.usecase.RefreshGetUsersUseCase
import com.benmohammad.coroutinesflow.domain.usecase.RemoveUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val domainModule = module {
    single<CoroutineDispatchers>{CoroutineDispatchersImpl()}
    single<UserRepository>{UserRepositoryImpl(get(), get(), responseToDomain = get<UserResponseToUserDomainMapper>(),
                                                            domainToResponse = get<UserDomainToUserResponseMapper>(),
                                                            domainToBody = get<UserDomainToUserBodyMapper>())}


    factory { GetUsersUseCase(get())}
    factory { RefreshGetUsersUseCase(get())}
    factory { RemoveUserUseCase(get())}
    factory { AddUserUseCase(get())}
}

