package com.koinapistructure.di

import com.koinapistructure.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule= module {
    viewModel {MainViewModel(get())}
}