package com.benmohammad.coroutinesflow.koin

import com.benmohammad.coroutinesflow.ui.add.AddVM
import com.benmohammad.coroutinesflow.ui.main.MainVM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module{
    viewModel { MainVM(get(), get(), get())}
    viewModel { AddVM(get())}
}