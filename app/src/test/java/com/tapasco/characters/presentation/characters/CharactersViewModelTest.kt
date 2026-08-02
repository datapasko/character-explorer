package com.tapasco.characters.presentation.characters

import androidx.paging.PagingData
import com.tapasco.characters.MainDispatcherRule
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.PreferencesRepository
import com.tapasco.characters.domain.usecase.GetCharactersUseCase
import com.tapasco.characters.domain.usecase.GetFavoriteCharacterIdsUseCase
import com.tapasco.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun favoriteIds_areExposedInUiState() = runTest {
        val preferencesRepository = FakePreferencesRepository()
        val viewModel = createViewModel(preferencesRepository = preferencesRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        preferencesRepository.favoriteCharacterIds.value = setOf(1, 7)
        runCurrent()

        assertEquals(setOf(1, 7), viewModel.uiState.value.favoriteCharacterIds)
    }

    @Test
    fun toggleFavorite_updatesPersistentFavorites() = runTest {
        val preferencesRepository = FakePreferencesRepository()
        val viewModel = createViewModel(preferencesRepository = preferencesRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.onEvent(CharactersEvent.OnToggleFavorite(characterId = 42))
        runCurrent()

        assertEquals(listOf(42), preferencesRepository.toggledCharacterIds)
        assertEquals(setOf(42), viewModel.uiState.value.favoriteCharacterIds)
    }

    @Test
    fun searchAndStatusChanges_areDebouncedAndSentToRepository() = runTest {
        val charactersRepository = FakeCharactersRepository()
        val viewModel = createViewModel(charactersRepository = charactersRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.characters.collect()
        }
        runCurrent()

        assertEquals(CharacterRequest(name = null, status = null), charactersRepository.requests.single())

        viewModel.onEvent(CharactersEvent.OnSearchQueryChange("  Rick  "))
        advanceTimeBy((SEARCH_DEBOUNCE_TEST_MILLIS - 1).milliseconds)
        runCurrent()
        assertEquals(1, charactersRepository.requests.size)

        advanceTimeBy(1.milliseconds)
        runCurrent()
        assertEquals(CharacterRequest(name = "Rick", status = null), charactersRepository.requests.last())

        viewModel.onEvent(CharactersEvent.OnStatusFilterChange(CharacterStatusFilter.Dead))
        advanceTimeBy(SEARCH_DEBOUNCE_TEST_MILLIS.milliseconds)
        runCurrent()

        assertEquals(CharacterRequest(name = "Rick", status = "dead"), charactersRepository.requests.last())
    }

    private fun createViewModel(
        charactersRepository: FakeCharactersRepository = FakeCharactersRepository(),
        preferencesRepository: FakePreferencesRepository = FakePreferencesRepository(),
    ) = CharactersViewModel(
        getCharactersUseCase = GetCharactersUseCase(charactersRepository),
        getFavoriteCharacterIdsUseCase = GetFavoriteCharacterIdsUseCase(preferencesRepository),
        toggleFavoriteCharacterUseCase = ToggleFavoriteCharacterUseCase(preferencesRepository),
    )
}

private class FakeCharactersRepository : CharactersRepository {
    val requests = mutableListOf<CharacterRequest>()

    override fun getCharacters(
        name: String?,
        status: String?,
    ): Flow<PagingData<Character>> {
        requests += CharacterRequest(name = name, status = status)
        return flowOf(PagingData.empty())
    }

    override fun observeCharacter(characterId: Int): Flow<Character?> = flowOf(null)

    override suspend fun refreshCharacter(
        characterId: Int,
        forceRefresh: Boolean,
    ): Result<Unit> = error("Not needed for CharactersViewModel tests")
}

private class FakePreferencesRepository : PreferencesRepository {
    override val favoriteCharacterIds = MutableStateFlow<Set<Int>>(emptySet())
    val toggledCharacterIds = mutableListOf<Int>()

    override suspend fun toggleFavoriteCharacter(characterId: Int) {
        toggledCharacterIds += characterId
        favoriteCharacterIds.value = favoriteCharacterIds.value
            .toMutableSet()
            .apply {
                if (!add(characterId)) {
                    remove(characterId)
                }
            }
    }
}

private data class CharacterRequest(
    val name: String?,
    val status: String?,
)

private const val SEARCH_DEBOUNCE_TEST_MILLIS = 350L
