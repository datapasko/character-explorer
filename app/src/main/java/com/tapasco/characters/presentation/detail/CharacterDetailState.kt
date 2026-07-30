package com.tapasco.characters.presentation.detail

import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.Episode

data class CharacterDetailState(
    val isLoading: Boolean = true,
    val character: Character? = null,
    val hasError: Boolean = false,
    val episodes: List<Episode> = emptyList(),
    val isEpisodesLoading: Boolean = false,
    val hasEpisodesError: Boolean = false,
)
