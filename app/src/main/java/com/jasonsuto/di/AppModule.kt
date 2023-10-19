package com.jasonsuto.di

import com.jasonsuto.currencyrates.data.RatesApi
import com.jasonsuto.currencyrates.main.NetworkMainRepository
import com.jasonsuto.currencyrates.main.MainRepository
import com.jasonsuto.currencyrates.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://api.exchangeratesapi.io/v1/"

// Put your singletons here.
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesCurrencyApi(): RatesApi= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory
                .create()
        )
        .build()
        .create(RatesApi::class.java)

    @Singleton
    @Provides
    fun providesMainRepository(api: RatesApi): MainRepository=NetworkMainRepository(api)

    @Singleton
    @Provides
    fun providesDispatchers(): DispatcherProvider=object: DispatcherProvider{
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfirmed: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

}