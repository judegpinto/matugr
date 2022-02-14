package com.matugr.di

import com.matugr.common.tools.DefaultDispatcherProvider
import com.matugr.common.tools.DispatcherProvider
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
class ScaffoldingModule {
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }
}