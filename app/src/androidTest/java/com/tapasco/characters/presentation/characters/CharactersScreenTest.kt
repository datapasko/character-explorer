package com.tapasco.characters.presentation.characters

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

    private fun setScreenContent(
        state: CharactersState,
        characters: List<Character>,
        onSearchQueryChange: (String) -> Unit = {},
        onStatusSelected: (CharacterStatusFilter) -> Unit = {},
        onToggleFavorite: (Int) -> Unit = {},
        onCharacterClick: (Int) -> Unit = {},
    ) {
        val pagingData = flowOf(PagingData.from(characters))

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

private fun testCharacter() = Character(
    id = 1,
    name = "Rick Sanchez",
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
