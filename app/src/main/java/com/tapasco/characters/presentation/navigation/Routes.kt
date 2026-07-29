package com.tapasco.characters.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object Characters: Routes()

    @Serializable
    data class CharacterDetail(
        val id: Int
    ): Routes()
}