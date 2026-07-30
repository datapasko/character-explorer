package com.tapasco.characters.presentation.characters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.ui.theme.CharactersMotion
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharactersScreen(
    onCharacterClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val characters = viewModel.characters.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val collapseThresholdPx = with(LocalDensity.current) {
        HEADER_COLLAPSE_THRESHOLD.roundToPx()
    }
    val isHeaderCollapsed by remember(listState, collapseThresholdPx) {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > collapseThresholdPx
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        CharacterFiltersHeader(
            searchQuery = state.searchQuery,
            selectedStatus = state.selectedStatus,
            isCollapsed = isHeaderCollapsed,
            onSearchQueryChange = { query ->
                viewModel.onEvent(CharactersEvent.OnSearchQueryChange(query))
            },
            onStatusSelected = { status ->
                viewModel.onEvent(CharactersEvent.OnStatusFilterChange(status))
            },
        )
        CharactersContent(
            characters = characters,
            listState = listState,
            favoriteCharacterIds = state.favoriteCharacterIds,
            onToggleFavorite = { characterId ->
                viewModel.onEvent(CharactersEvent.OnToggleFavorite(characterId))
            },
            onCharacterClick = onCharacterClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CharacterFiltersHeader(
    searchQuery: String,
    selectedStatus: CharacterStatusFilter,
    isCollapsed: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onStatusSelected: (CharacterStatusFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            AnimatedVisibility(
                visible = !isCollapsed,
                enter = fadeIn(
                    animationSpec = CharactersMotion.quickFade,
                ) + expandVertically(
                    animationSpec = CharactersMotion.headerResize,
                    expandFrom = Alignment.Top,
                ),
                exit = fadeOut(
                    animationSpec = CharactersMotion.quickFade,
                ) + shrinkVertically(
                    animationSpec = CharactersMotion.headerResize,
                    shrinkTowards = Alignment.Top,
                ),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.characters_screen_title),
                        modifier = Modifier.semantics { heading() },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = stringResource(R.string.characters_screen_description),
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(text = stringResource(R.string.search_by_name))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() },
                ),
                shape = RoundedCornerShape(18.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.status_filter_subtitle),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .semantics {
                        isTraversalGroup = true
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CharacterStatusFilter.entries.forEach { status ->
                    val isSelected = selectedStatus == status

                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusSelected(status) },
                        label = {
                            Text(text = stringResource(status.labelRes))
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun CharactersContent(
    characters: LazyPagingItems<Character>,
    listState: LazyListState,
    favoriteCharacterIds: Set<Int>,
    onToggleFavorite: (characterId: Int) -> Unit,
    onCharacterClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loadingCharactersDescription = stringResource(R.string.characters_loading)
    val loadingMoreDescription = stringResource(R.string.characters_loading_more)

    Box(modifier = modifier.fillMaxSize()) {
        when (characters.loadState.refresh) {
            is LoadState.Loading -> {
                CharacterSkeletonList(
                    contentDescription = loadingCharactersDescription,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is LoadState.Error if characters.itemCount == 0 -> {
                ErrorContent(
                    onRetry = characters::retry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is LoadState.NotLoading if characters.itemCount == 0 -> {
                Text(
                    text = stringResource(R.string.characters_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                        },
                )
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            collectionInfo = CollectionInfo(
                                rowCount = characters.itemCount,
                                columnCount = 1,
                            )
                        },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        count = characters.itemCount,
                        key = characters.itemKey { it.id },
                    ) { index ->
                        characters[index]?.let { character ->
                            CharacterCard(
                                character = character,
                                isFavorite = character.id in favoriteCharacterIds,
                                onFavoriteClick = { onToggleFavorite(character.id) },
                                onClick = { onCharacterClick(character.id) },
                                modifier = Modifier.semantics {
                                    collectionItemInfo = CollectionItemInfo(
                                        rowIndex = index,
                                        rowSpan = 1,
                                        columnIndex = 0,
                                        columnSpan = 1,
                                    )
                                },
                            )
                        }
                    }

                    when (characters.loadState.append) {
                        is LoadState.Loading -> {
                            item(key = APPEND_SKELETON_KEY) {
                                CharacterCardSkeleton(
                                    modifier = Modifier.clearAndSetSemantics {
                                        contentDescription = loadingMoreDescription
                                        liveRegion = LiveRegionMode.Polite
                                    },
                                )
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Button(
                                    onClick = characters::retry,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                ) {
                                    Text(text = stringResource(R.string.retry))
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

private val HEADER_COLLAPSE_THRESHOLD = 48.dp
private const val APPEND_SKELETON_KEY = "append_skeleton"

@Composable
private fun ErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorMessage = stringResource(R.string.characters_load_error)

    Column(
        modifier = modifier
            .padding(24.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                error(errorMessage)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.nave_rick),
            contentDescription = null,
            modifier = Modifier
                .width(280.dp)
                .height(170.dp),
            contentScale = ContentScale.Crop,
        )

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
        )

        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}
