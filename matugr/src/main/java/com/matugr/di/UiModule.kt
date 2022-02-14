package com.matugr.di

import androidx.lifecycle.ViewModelProvider
import com.matugr.ui.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class UiModule {
    @Binds
    internal abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}