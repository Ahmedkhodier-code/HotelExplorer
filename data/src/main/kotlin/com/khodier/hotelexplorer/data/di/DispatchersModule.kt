package com.khodier.hotelexplorer.data.di

import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.common.dispatcher.DefaultCoroutineDispatchers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatchersModule {

    @Binds
    abstract fun bindCoroutineDispatchers(impl: DefaultCoroutineDispatchers): CoroutineDispatchers
}