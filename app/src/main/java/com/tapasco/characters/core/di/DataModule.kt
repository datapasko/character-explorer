package com.tapasco.characters.core.di

import com.tapasco.characters.data.repository.CharactersRepositoryImpl
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import com.tapasco.characters.presentation.characters.CharactersViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<CharactersRepository> {
        CharactersRepositoryImpl(api = get())
    }

    factory {
        GetCharactersUseCase(repository = get())
    }

    factory {
        GetCharacterUseCase(repository = get())
    }

    viewModelOf(::CharactersViewModel)
}
