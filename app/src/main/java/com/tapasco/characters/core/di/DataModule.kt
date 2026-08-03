package com.tapasco.characters.core.di

import androidx.room.Room
import com.tapasco.characters.core.utils.SystemTimeProvider
import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.data.local.CharactersDatabase
import com.tapasco.characters.data.preferences.PreferencesRepositoryImpl
import com.tapasco.characters.data.repository.CharactersRepositoryImpl
import com.tapasco.characters.data.repository.EpisodesRepositoryImpl
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.EpisodesRepository
import com.tapasco.characters.domain.repository.PreferencesRepository
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

    single<PreferencesRepository> {
        PreferencesRepositoryImpl(context = get())
    }
}
