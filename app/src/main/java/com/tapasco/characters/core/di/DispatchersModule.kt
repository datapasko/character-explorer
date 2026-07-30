package com.tapasco.characters.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val IoDispatcherQualifier = named("IoDispatcher")

val dispatchersModule = module {
    single<CoroutineDispatcher>(IoDispatcherQualifier) {
        Dispatchers.IO
    }
}
