package com.kiriuru.weatherappsev.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module


@Module
interface ViewModelBuilderModule {
    @Binds
    fun bindViewModelFactory(
        factory: MainViewModelFactory
    ): ViewModelProvider.Factory
}