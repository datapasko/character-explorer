package com.tapasco.characters.core.di

import com.tapasco.characters.data.repository.CharactersRepositoryImpl
import com.tapasco.characters.data.repository.EpisodesRepositoryImpl
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.EpisodesRepository
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import com.tapasco.characters.presentation.characters.CharactersViewModel
import com.tapasco.characters.presentation.detail.CharacterDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<CharactersRepository> {
        CharactersRepositoryImpl(api = get())
    }

    single<EpisodesRepository> {
        EpisodesRepositoryImpl(
            api = get(),
            ioDispatcher = get(IoDispatcherQualifier),
        )
    }

    factory {
        GetCharactersUseCase(repository = get())
    }

    factory {
        GetCharacterUseCase(repository = get())
    }

    factory {
        GetEpisodesUseCase(repository = get())
    }

    viewModelOf(::CharactersViewModel)

    viewModel { parameters ->
        CharacterDetailViewModel(
            characterId = parameters.get(),
            getCharacterUseCase = get(),
            getEpisodesUseCase = get(),
        )
    }
}
