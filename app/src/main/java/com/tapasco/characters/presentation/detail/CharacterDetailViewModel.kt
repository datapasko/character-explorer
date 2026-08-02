package com.tapasco.characters.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import com.tapasco.characters.domain.usecase.RefreshCharacterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val characterId: Int,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val refreshCharacterUseCase: RefreshCharacterUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state: StateFlow<CharacterDetailState> = _state.asStateFlow()

    private var loadedEpisodeUrls: List<String>? = null

    init {
        observeCharacter()
        refreshCharacter()
    }

    fun retry() {
        refreshCharacter(forceRefresh = true)
    }

    fun retryEpisodes() {
        _state.value.character?.let(::loadEpisodes)
    }

    private fun observeCharacter() {
        viewModelScope.launch {
            getCharacterUseCase(characterId).collectLatest { character ->
                if (character == null) return@collectLatest

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        hasError = false,
                        character = character,
                    )
                }

                if (loadedEpisodeUrls != character.episodeUrls) {
                    loadedEpisodeUrls = character.episodeUrls
                    loadEpisodes(character)
                }
            }
        }
    }

    private fun refreshCharacter(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = currentState.character == null,
                    hasError = false,
                )
            }

            refreshCharacterUseCase(
                characterId = characterId,
                forceRefresh = forceRefresh,
            ).onFailure {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        hasError = currentState.character == null,
                    )
                }
            }
        }
    }

    private fun loadEpisodes(character: Character) {
        if (character.episodeUrls.isEmpty()) {
            _state.update { currentState ->
                currentState.copy(
                    episodes = emptyList(),
                    isEpisodesLoading = false,
                    hasEpisodesError = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isEpisodesLoading = true,
                    hasEpisodesError = false,
                )
            }

            getEpisodesUseCase(character.episodeUrls).fold(
                onSuccess = { episodes ->
                    _state.update { currentState ->
                        currentState.copy(
                            episodes = episodes,
                            isEpisodesLoading = false,
                        )
                    }
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(
                            isEpisodesLoading = false,
                            hasEpisodesError = true,
                        )
                    }
                },
            )
        }
    }
}
