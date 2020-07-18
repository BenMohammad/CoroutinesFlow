package com.benmohammad.coroutinesflow.domain.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {

    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
}