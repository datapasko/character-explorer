package com.tapasco.characters.presentation.characters

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.CharacterLocation
import com.tapasco.characters.ui.theme.CharactersTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CharactersScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun favoriteCharacter_isSelectedAndForwardsUserActions() {
        var favoriteCharacterId: Int? = null
        var openedCharacterId: Int? = null
        val character = testCharacter()

        setScreenContent(
            state = CharactersState(favoriteCharacterIds = setOf(character.id)),
            characters = listOf(character),
            onToggleFavorite = { favoriteCharacterId = it },
            onCharacterClick = { openedCharacterId = it },
        )
        waitForCharacter(character.name)

        composeRule
            .onNodeWithContentDescription(
                composeRule.activity.getString(R.string.remove_from_favorites),
            )
            .assertIsOn()
            .performClick()

        composeRule.onNodeWithText(character.name)
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(character.id, favoriteCharacterId)
            assertEquals(character.id, openedCharacterId)
        }
    }

    @Test
    fun statusFilter_forwardsSelectedStatus() {
        var selectedStatus: CharacterStatusFilter? = null

        setScreenContent(
            state = CharactersState(),
            characters = listOf(testCharacter()),
            onStatusSelected = { selectedStatus = it },
        )

        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.status_all))
            .assertIsSelected()

        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.status_dead))
            .performClick()

        composeRule.runOnIdle {
            assertEquals(CharacterStatusFilter.Dead, selectedStatus)
        }
    }

    @Test
    fun statusFilterChange_scrollsCharactersToTop() {
        val characters = (1..12).map(::testCharacter)
        val pagingData = flowOf(
            PagingData.from(
                data = characters,
                sourceLoadStates = COMPLETED_LOAD_STATES,
            ),
        )

        composeRule.setContent {
            var state by remember { mutableStateOf(CharactersState()) }
            val lazyCharacters = pagingData.collectAsLazyPagingItems()

            CharactersTheme {
                CharactersScreenContent(
                    state = state,
                    characters = lazyCharacters,
                    onSearchQueryChange = {},
                    onStatusSelected = { selectedStatus ->
                        state = state.copy(selectedStatus = selectedStatus)
                    },
                    onToggleFavorite = {},
                    onCharacterClick = {},
                )
            }
        }

        waitForCharacter(characters.first().name)

        composeRule
            .onNodeWithTag(CHARACTERS_LIST_TEST_TAG)
            .performScrollToIndex(characters.lastIndex)

        composeRule
            .onNodeWithText(characters.last().name)
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.status_dead))
            .performClick()

        composeRule
            .onNodeWithText(characters.first().name)
            .assertIsDisplayed()
    }

    private fun setScreenContent(
        state: CharactersState,
        characters: List<Character>,
        onSearchQueryChange: (String) -> Unit = {},
        onStatusSelected: (CharacterStatusFilter) -> Unit = {},
        onToggleFavorite: (Int) -> Unit = {},
        onCharacterClick: (Int) -> Unit = {},
    ) {
        val pagingData = flowOf(
            PagingData.from(
                data = characters,
                sourceLoadStates = COMPLETED_LOAD_STATES,
            ),
        )

        composeRule.setContent {
            val lazyCharacters = pagingData.collectAsLazyPagingItems()

            CharactersTheme {
                CharactersScreenContent(
                    state = state,
                    characters = lazyCharacters,
                    onSearchQueryChange = onSearchQueryChange,
                    onStatusSelected = onStatusSelected,
                    onToggleFavorite = onToggleFavorite,
                    onCharacterClick = onCharacterClick,
                )
            }
        }
    }

    private fun waitForCharacter(characterName: String) {
        composeRule.waitUntil(timeoutMillis = UI_TEST_TIMEOUT_MILLIS) {
            composeRule
                .onAllNodesWithText(characterName)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}

private fun testCharacter(
    id: Int = 1,
) = Character(
    id = id,
    name = if (id == 1) "Rick Sanchez" else "Character $id",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = CharacterLocation(name = "Earth", url = ""),
    location = CharacterLocation(name = "Citadel of Ricks", url = ""),
    imageUrl = "",
    episodeUrls = emptyList(),
    createdAt = "",
)

private const val UI_TEST_TIMEOUT_MILLIS = 5_000L

private val COMPLETED_LOAD_STATES = LoadStates(
    refresh = LoadState.NotLoading(endOfPaginationReached = true),
    prepend = LoadState.NotLoading(endOfPaginationReached = true),
    append = LoadState.NotLoading(endOfPaginationReached = true),
)
