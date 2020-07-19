package com.benmohammad.coroutinesflow.koin

import com.benmohammad.coroutinesflow.BuildConfig
import com.benmohammad.coroutinesflow.data.mapper.UserDomainToUserBodyMapper
import com.benmohammad.coroutinesflow.data.mapper.UserDomainToUserResponseMapper
import com.benmohammad.coroutinesflow.data.mapper.UserResponseToUserDomainMapper
import com.benmohammad.coroutinesflow.data.remote.UserApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val BASE_URL = "BASE_URL"

val dataModule = module {
    single{UserApiService(get())}
    single{UserResponseToUserDomainMapper()}
    single{UserDomainToUserResponseMapper()}
    single{UserDomainToUserBodyMapper()}
    single{provideRetrofit(get(named(BASE_URL)), get(), get())}
    single{provideMoshi()}
    single{provideokHttpClient()}

    single(named(BASE_URL)) {"https://5caad70369c15c001484956a.mockapi.io/hoc081098/"}
}

private fun provideMoshi(): Moshi {
    return Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

private fun provideRetrofit(baseUrl: String, moshi: Moshi, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build()
}

private fun provideokHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor()
            .apply { level = if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }
        )
        .build()
}