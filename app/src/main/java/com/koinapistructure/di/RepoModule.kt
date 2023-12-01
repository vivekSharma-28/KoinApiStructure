package com.koinapistructure.di

import com.koinapistructure.api.Api
import com.koinapistructure.repository.ApiRepository
import org.koin.dsl.module

val RepoModule= module {
    single{ ApiRepository(get())}
}