package com.tapasco.characters.presentation.detail

import androidx.activity.ComponentActivity
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.paging.PagingData
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.CharacterLocation
import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.EpisodesRepository
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import com.tapasco.characters.domain.usecase.RefreshCharacterUseCase
import com.tapasco.characters.presentation.mapper.labelRes
import com.tapasco.characters.ui.theme.CharactersTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CharacterDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun screenEntry_loadsCharacterUsingRouteId() {
        val characterId = 137
        val repository = FakeScreenCharactersRepository()
        val viewModel = CharacterDetailViewModel(
            getCharacterUseCase = GetCharacterUseCase(repository),
            refreshCharacterUseCase = RefreshCharacterUseCase(repository),
            getEpisodesUseCase = GetEpisodesUseCase(FakeScreenEpisodesRepository()),
        )

        composeRule.setContent {
            CharactersTheme {
                CharacterDetailScreen(
                    characterId = characterId,
                    viewModel = viewModel,
                )
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            repository.observedIds == listOf(characterId) &&
                repository.refreshedIds == listOf(characterId)
        }
    }

    @Test
    fun loadingState_showsAccessibleSkeletonOnly() {
        setScreenContent(uiState = CharacterDetailState())

        composeRule
            .onNodeWithContentDescription(
                getString(R.string.character_detail_loading),
            )
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(TEST_CHARACTER_NAME)
            .assertDoesNotExist()
    }

    @Test
    fun characterError_showsMessageAndForwardsRetry() {
        var retryCount = 0

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                hasError = true,
            ),
            onRetry = { retryCount++ },
        )

        composeRule
            .onNodeWithText(getString(R.string.character_detail_load_error))
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(getString(R.string.retry))
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, retryCount)
        }
    }

    @Test
    fun loadedCharacter_showsIdentityAndAccessiblePortrait() {
        val character = testCharacter()

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                character = character,
            ),
        )

        composeRule
            .onNodeWithContentDescription(
                getString(
                    R.string.character_portrait_description,
                    character.name,
                ),
            )
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(character.name)
            .assertIsDisplayed()
            .assert(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading),
            )

        composeRule
            .onNodeWithText(
                getString(
                    R.string.character_species_gender,
                    character.species,
                    getString(character.gender.labelRes),
                ),
            )
            .assertIsDisplayed()
    }

    @Test
    fun shareButton_forwardsLoadedCharacter() {
        val character = testCharacter()
        var sharedCharacter: Character? = null

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                character = character,
            ),
            onShareCharacter = { sharedCharacter = it },
        )

        composeRule
            .onNodeWithContentDescription(
                getString(R.string.share_character, character.name),
            )
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(character, sharedCharacter)
        }
    }

    @Test
    fun episodesLoading_showsAccessibleProgressState() {
        val character = testCharacter()

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                character = character,
                isEpisodesLoading = true,
            ),
        )
        scrollToText(getString(R.string.character_episodes_label))

        composeRule
            .onNodeWithContentDescription(
                getString(R.string.character_episodes_loading),
            )
            .assertIsDisplayed()
    }

    @Test
    fun episodesError_keepsCharacterAndForwardsRetry() {
        val character = testCharacter()
        var retryCount = 0

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                character = character,
                hasEpisodesError = true,
            ),
            onRetryEpisodes = { retryCount++ },
        )

        composeRule
            .onNodeWithText(character.name)
            .assertExists()

        scrollToText(getString(R.string.retry_episodes))

        composeRule
            .onNodeWithText(getString(R.string.character_episodes_load_error))
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(getString(R.string.retry_episodes))
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, retryCount)
        }
    }

    @Test
    fun emptyEpisodes_showsEmptyState() {
        val character = testCharacter(episodeUrls = emptyList())

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                character = character,
            ),
        )
        scrollToText(getString(R.string.character_episodes_empty))

        composeRule
            .onNodeWithText(getString(R.string.character_episodes_empty))
            .assertIsDisplayed()
    }

    @Test
    fun loadedEpisodes_showCountAndEpisodeInformation() {
        val character = testCharacter()
        val episode = testEpisode()

        setScreenContent(
            uiState = CharacterDetailState(
                isLoading = false,
                character = character,
                episodes = listOf(episode),
            ),
        )
        scrollToText(getString(R.string.character_episodes_label))

        composeRule
            .onNodeWithText(
                getQuantityString(
                    R.plurals.character_episode_count,
                    character.episodeUrls.size,
                    character.episodeUrls.size,
                ),
            )
            .assertIsDisplayed()
        composeRule.onNodeWithText(episode.code).assertIsDisplayed()
        composeRule.onNodeWithText(episode.name).assertIsDisplayed()
        composeRule.onNodeWithText(episode.airDate).assertIsDisplayed()
    }

    private fun setScreenContent(
        uiState: CharacterDetailState,
        onRetry: () -> Unit = {},
        onRetryEpisodes: () -> Unit = {},
        onShareCharacter: (Character) -> Unit = {},
    ) {
        composeRule.setContent {
            CharactersTheme {
                CharacterDetailContent(
                    uiState = uiState,
                    onRetry = onRetry,
                    onRetryEpisodes = onRetryEpisodes,
                    onShareCharacter = onShareCharacter,
                )
            }
        }
    }

    private fun scrollToText(text: String) {
        composeRule
            .onAllNodes(hasScrollAction())[0]
            .performScrollToNode(hasText(text))
    }

    private fun getString(
        @StringRes resourceId: Int,
        vararg formatArgs: Any,
    ): String = composeRule.activity.getString(resourceId, *formatArgs)

    private fun getQuantityString(
        @PluralsRes resourceId: Int,
        quantity: Int,
        vararg formatArgs: Any,
    ): String = composeRule.activity.resources.getQuantityString(
        resourceId,
        quantity,
        *formatArgs,
    )
}

private fun testCharacter(
    episodeUrls: List<String> = listOf(
        "https://rickandmortyapi.com/api/episode/1",
        "https://rickandmortyapi.com/api/episode/2",
    ),
) = Character(
    id = 1,
    name = TEST_CHARACTER_NAME,
    status = StatusCharacter.ALIVE,
    species = "Human",
    type = "",
    gender = GenderCharacter.MALE,
    origin = CharacterLocation(
        name = "Earth (C-137)",
        url = "",
    ),
    location = CharacterLocation(
        name = "Citadel of Ricks",
        url = "",
    ),
    imageUrl = "",
    episodeUrls = episodeUrls,
    createdAt = "2017-11-04",
)

private fun testEpisode() = Episode(
    id = 1,
    name = "Pilot",
    airDate = "December 2, 2013",
    code = "S01E01",
)

private class FakeScreenCharactersRepository : CharactersRepository {
    val observedIds = mutableListOf<Int>()
    val refreshedIds = mutableListOf<Int>()

    override fun getCharacters(
        name: String?,
        status: String?,
    ): Flow<PagingData<Character>> = flowOf(PagingData.empty())

    override fun observeCharacter(characterId: Int): Flow<Character?> {
        observedIds += characterId
        return flowOf(null)
    }

    override suspend fun refreshCharacter(
        characterId: Int,
        forceRefresh: Boolean,
    ): Result<Unit> {
        refreshedIds += characterId
        return Result.success(Unit)
    }
}

private class FakeScreenEpisodesRepository : EpisodesRepository {
    override suspend fun getEpisodes(episodeIds: List<Int>): Result<List<Episode>> = Result.success(emptyList())
}

private const val TEST_CHARACTER_NAME = "Rick Sanchez"
