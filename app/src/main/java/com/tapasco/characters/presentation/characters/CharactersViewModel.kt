package com.tapasco.characters.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class CharactersViewModel(
    getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharactersState())
    val state = _state.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val characters = state
        .map { currentState ->
            CharacterFilters(
                name = currentState.searchQuery,
                status = currentState.selectedStatus.apiValue,
            )
        }
        .distinctUntilChanged()
        .debounce { filters ->
            if (filters.name.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS
        }
        .flatMapLatest { filters ->
            getCharactersUseCase(
                name = filters.name,
                status = filters.status,
            )
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: CharactersEvent) {
        when (event) {
            is CharactersEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
            }

            is CharactersEvent.OnStatusFilterChange -> {
                _state.update { it.copy(selectedStatus = event.status) }
            }

            is CharactersEvent.OnToggleFavorite -> {
                _state.update { currentState ->
                    val updatedFavorites = currentState.favoriteCharacterIds.toMutableSet()
                    if (!updatedFavorites.add(event.characterId)) {
                        updatedFavorites.remove(event.characterId)
                    }
                    currentState.copy(favoriteCharacterIds = updatedFavorites)
                }
            }

            CharactersEvent.OnCharactersLoadingFinished -> Unit
        }
    }
}

private const val SEARCH_DEBOUNCE_MILLIS = 350L

private data class CharacterFilters(
    val name: String,
    val status: String?,
)
