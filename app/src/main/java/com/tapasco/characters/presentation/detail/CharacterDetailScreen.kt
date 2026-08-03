package com.tapasco.characters.presentation.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.presentation.mapper.labelRes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    modifier: Modifier = Modifier,
    viewModel: CharacterDetailViewModel = koinViewModel(
        key = "character-detail-$characterId",
    ),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(characterId, viewModel) {
        viewModel.loadCharacter(characterId)
    }

    CharacterDetailContent(
        uiState = uiState,
        onRetry = { viewModel.retry(characterId) },
        onRetryEpisodes = viewModel::retryEpisodes,
        onShareCharacter = { character ->
            shareCharacter(
                context = context,
                character = character,
            )
        },
        modifier = modifier,
    )
}

@Composable
internal fun CharacterDetailContent(
    uiState: CharacterDetailState,
    onRetry: () -> Unit,
    onRetryEpisodes: () -> Unit,
    onShareCharacter: (Character) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> CharacterDetailLoading(modifier)

        uiState.hasError -> CharacterDetailError(
            onRetry = onRetry,
            modifier = modifier,
        )

        uiState.character != null -> CharacterDetailSuccess(
            character = uiState.character,
            episodes = uiState.episodes,
            isEpisodesLoading = uiState.isEpisodesLoading,
            hasEpisodesError = uiState.hasEpisodesError,
            onRetryEpisodes = onRetryEpisodes,
            onShareCharacter = onShareCharacter,
            modifier = modifier,
        )

        else -> CharacterDetailError(
            onRetry = onRetry,
            modifier = modifier,
        )
    }
}

@Composable
private fun CharacterDetailSuccess(
    character: Character,
    episodes: List<Episode>,
    isEpisodesLoading: Boolean,
    hasEpisodesError: Boolean,
    onRetryEpisodes: () -> Unit,
    onShareCharacter: (Character) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "hero") {
            CharacterDetailHero(character = character)
        }

        item(key = "identity") {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = character.name,
                        modifier = Modifier.semantics { heading() },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(
                            R.string.character_species_gender,
                            character.species,
                            stringResource(character.gender.labelRes),
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }

                Surface(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                ) {
                    IconButton(
                        onClick = {
                            onShareCharacter(character)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(
                                R.string.share_character,
                                character.name,
                            ),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        item(key = "origin") {
            CharacterInfoCard(
                label = stringResource(R.string.character_origin_label),
                value = character.origin.name,
            )
        }

        item(key = "location") {
            CharacterInfoCard(
                label = stringResource(R.string.character_location_label),
                value = character.location.name,
            )
        }

        item(key = "episodes") {
            EpisodesSection(
                episodeCount = character.episodeUrls.size,
                episodes = episodes,
                isLoading = isEpisodesLoading,
                hasError = hasEpisodesError,
                onRetry = onRetryEpisodes,
            )
        }
    }
}

@Composable
private fun EpisodesSection(
    episodeCount: Int,
    episodes: List<Episode>,
    isLoading: Boolean,
    hasError: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column {
            Text(
                text = stringResource(R.string.character_episodes_label),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = pluralStringResource(
                    R.plurals.character_episode_count,
                    episodeCount,
                    episodeCount,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        when {
            isLoading -> EpisodesLoading()
            hasError -> EpisodesError(onRetry = onRetry)
            episodes.isEmpty() -> Text(
                text = stringResource(R.string.character_episodes_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )

            else -> LazyRow(
                modifier = Modifier.semantics {
                    collectionInfo = CollectionInfo(
                        rowCount = 1,
                        columnCount = episodes.size,
                    )
                },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                itemsIndexed(
                    items = episodes,
                    key = { _, episode -> episode.id },
                ) { index, episode ->
                    EpisodeCard(
                        episode = episode,
                        modifier = Modifier.semantics {
                            collectionItemInfo = CollectionItemInfo(
                                rowIndex = 0,
                                rowSpan = 1,
                                columnIndex = index,
                                columnSpan = 1,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: Episode,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(236.dp)
            .heightIn(min = 50.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = episode.code,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = episode.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = episode.airDate,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun EpisodesLoading(
    modifier: Modifier = Modifier,
) {
    val loadingDescription = stringResource(R.string.character_episodes_loading)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 132.dp)
            .semantics {
                contentDescription = loadingDescription
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun EpisodesError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.character_episodes_load_error),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_episodes))
            }
        }
    }
}

@Composable
private fun CharacterInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun CharacterDetailLoading(
    modifier: Modifier = Modifier,
) {
    val loadingDescription = stringResource(R.string.character_detail_loading)

    CharacterDetailSkeleton(
        contentDescription = loadingDescription,
        modifier = modifier,
    )
}

@Composable
private fun CharacterDetailError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorDescription = stringResource(R.string.character_detail_load_error)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .semantics {
                liveRegion = LiveRegionMode.Assertive
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = errorDescription,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 20.dp),
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

private fun shareCharacter(
    context: Context,
    character: Character,
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, character.name)
        putExtra(
            Intent.EXTRA_TEXT,
            context.getString(
                R.string.share_character_text,
                character.name,
                context.getString(character.status.labelRes),
                character.species,
                character.imageUrl,
            ),
        )
    }

    context.startActivity(
        Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_character_chooser_title),
        ),
    )
}
