package com.tapasco.characters.presentation.characters

sealed interface CharactersEvent {

    data class OnSearchQueryChange(
        val query: String,
    ) : CharactersEvent

    data class OnStatusFilterChange(
        val status: CharacterStatusFilter,
    ) : CharactersEvent

    data class OnToggleFavorite(
        val characterId: Int,
    ) : CharactersEvent
}
