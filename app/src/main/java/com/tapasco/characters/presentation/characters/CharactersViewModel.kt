package com.tapasco.characters.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tapasco.characters.domain.repository.PreferencesRepository
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharactersViewModel(
    getCharactersUseCase: GetCharactersUseCase,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val filters = MutableStateFlow(CharacterFilters())

    val uiState: StateFlow<CharactersState> = combine(
        filters,
        preferencesRepository.favoriteCharacterIds,
    ) { filters, favoriteCharacterIds ->
        CharactersState(
            searchQuery = filters.searchQuery,
            selectedStatus = filters.selectedStatus,
            favoriteCharacterIds = favoriteCharacterIds,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(UI_STATE_STOP_TIMEOUT_MILLIS),
        initialValue = CharactersState(),
    )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val characters = filters
        .debounce { filters ->
            if (filters.searchQuery.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS
        }
        .flatMapLatest { filters ->
            getCharactersUseCase(
                name = filters.searchQuery,
                status = filters.selectedStatus.apiValue,
            )
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: CharactersEvent) {
        when (event) {
            is CharactersEvent.OnSearchQueryChange -> updateSearchQuery(event.query)
            is CharactersEvent.OnStatusFilterChange -> updateStatusFilter(event.status)
            is CharactersEvent.OnToggleFavorite -> toggleFavorite(event.characterId)
        }
    }

    private fun updateSearchQuery(query: String) {
        filters.update { currentFilters ->
            currentFilters.copy(searchQuery = query)
        }
    }

    private fun updateStatusFilter(status: CharacterStatusFilter) {
        filters.update { currentFilters ->
            currentFilters.copy(selectedStatus = status)
        }
    }

    private fun toggleFavorite(characterId: Int) {
        viewModelScope.launch {
            preferencesRepository.toggleFavoriteCharacter(characterId)
        }
    }
}

private const val SEARCH_DEBOUNCE_MILLIS = 350L
private const val UI_STATE_STOP_TIMEOUT_MILLIS = 5_000L

private data class CharacterFilters(
    val searchQuery: String = "",
    val selectedStatus: CharacterStatusFilter = CharacterStatusFilter.All,
)
