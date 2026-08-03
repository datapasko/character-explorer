package com.tapasco.characters.core.di

import com.tapasco.characters.presentation.characters.CharactersViewModel
import com.tapasco.characters.presentation.detail.CharacterDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::CharactersViewModel)
    viewModelOf(::CharacterDetailViewModel)
}
