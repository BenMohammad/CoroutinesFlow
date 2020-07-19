package com.benmohammad.coroutinesflow

import android.app.Application
import com.benmohammad.coroutinesflow.koin.dataModule
import com.benmohammad.coroutinesflow.koin.domainModule
import com.benmohammad.coroutinesflow.koin.viewModelModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
@Suppress("unused")
@FlowPreview
@ExperimentalCoroutinesApi
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            androidLogger()

            modules(
                dataModule,
                domainModule,
                viewModelModule
            )
        }
    }
}