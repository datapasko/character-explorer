package com.tapasco.characters.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val characterId: Int,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state: StateFlow<CharacterDetailState> = _state.asStateFlow()

    init {
        loadCharacter()
    }

    fun retry() {
        loadCharacter()
    }

    fun retryEpisodes() {
        _state.value.character?.let(::loadEpisodes)
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    hasError = false,
                )
            }
            getCharacterUseCase(characterId).fold(
                onSuccess = ::onCharacterLoaded,
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            hasError = true,
                        )
                    }
                },
            )
        }
    }

    private fun onCharacterLoaded(character: Character) {
        _state.update { currentState ->
            currentState.copy(
                isLoading = false,
                hasError = false,
                character = character,
                episodes = emptyList(),
                isEpisodesLoading = character.episodeUrls.isNotEmpty(),
                hasEpisodesError = false,
            )
        }

        loadEpisodes(character)
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
