package com.tapasco.characters.core.di

import com.tapasco.characters.data.repository.CharactersRepositoryImpl
import com.tapasco.characters.domain.repository.CharactersRepository
import org.koin.dsl.module

val dataModule = module {
    single<CharactersRepository> {
        CharactersRepositoryImpl(api = get())
    }

}