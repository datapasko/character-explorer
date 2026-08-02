package com.tapasco.characters.core.di

import com.tapasco.characters.data.preferences.PreferencesRepositoryImpl
import com.tapasco.characters.domain.repository.PreferencesRepository
import org.koin.dsl.module

val appModule = module {
    single<PreferencesRepository> {
        PreferencesRepositoryImpl(context = get())
    }
}
