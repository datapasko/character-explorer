package com.tapasco.characters.presentation.characters

import androidx.annotation.StringRes
import com.tapasco.characters.R

data class CharactersState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedStatus: CharacterStatusFilter = CharacterStatusFilter.All,
    val favoriteCharacterIds: Set<Int> = emptySet(),
)

enum class CharacterStatusFilter(
    @param:StringRes val labelRes: Int,
    val apiValue: String?,
) {
    All(
        labelRes = R.string.status_all,
        apiValue = null,
    ),
    Alive(
        labelRes = R.string.status_alive,
        apiValue = "alive",
    ),
    Dead(
        labelRes = R.string.status_dead,
        apiValue = "dead",
    ),
    Unknown(
        labelRes = R.string.status_unknown,
        apiValue = "unknown",
    ),
}
