package com.tapasco.characters.core.di

import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import com.tapasco.characters.domain.usecase.GetFavoriteCharacterIdsUseCase
import com.tapasco.characters.domain.usecase.RefreshCharacterUseCase
import com.tapasco.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetCharactersUseCase(repository = get()) }
    factory { GetCharacterUseCase(repository = get()) }
    factory { RefreshCharacterUseCase(repository = get()) }
    factory { GetEpisodesUseCase(repository = get()) }
    factory { GetFavoriteCharacterIdsUseCase(repository = get()) }
    factory { ToggleFavoriteCharacterUseCase(repository = get()) }
}
