package com.tapasco.characters.core.di

import androidx.room.Room
import com.tapasco.characters.core.utils.SystemTimeProvider
import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.data.local.CharactersDatabase
import com.tapasco.characters.data.repository.CharactersRepositoryImpl
import com.tapasco.characters.data.repository.EpisodesRepositoryImpl
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.EpisodesRepository
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import com.tapasco.characters.domain.usecase.GetFavoriteCharacterIdsUseCase
import com.tapasco.characters.domain.usecase.RefreshCharacterUseCase
import com.tapasco.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import com.tapasco.characters.presentation.characters.CharactersViewModel
import com.tapasco.characters.presentation.detail.CharacterDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<TimeProvider> {
        SystemTimeProvider
    }

    single {
        Room.databaseBuilder(
            context = get(),
            klass = CharactersDatabase::class.java,
            name = "characters.db",
        ).build()
    }

    single<CharactersRepository> {
        CharactersRepositoryImpl(
            api = get(),
            database = get(),
            timeProvider = get(),
            ioDispatcher = get(IoDispatcherQualifier),
        )
    }

    single<EpisodesRepository> {
        EpisodesRepositoryImpl(
            api = get(),
            episodeDao = get<CharactersDatabase>().episodeDao(),
            timeProvider = get(),
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
        RefreshCharacterUseCase(repository = get())
    }

    factory {
        GetEpisodesUseCase(repository = get())
    }

    factory {
        GetFavoriteCharacterIdsUseCase(repository = get())
    }

    factory {
        ToggleFavoriteCharacterUseCase(repository = get())
    }

    viewModelOf(::CharactersViewModel)

    viewModel { parameters ->
        CharacterDetailViewModel(
            characterId = parameters.get(),
            getCharacterUseCase = get(),
            refreshCharacterUseCase = get(),
            getEpisodesUseCase = get(),
        )
    }
}
