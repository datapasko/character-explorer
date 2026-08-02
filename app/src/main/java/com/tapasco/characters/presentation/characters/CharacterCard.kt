package com.tapasco.characters.presentation.characters

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.StatusCharacter
import com.tapasco.characters.presentation.mapper.labelRes
import com.tapasco.characters.ui.theme.CharactersMotion
import com.tapasco.characters.ui.theme.FavoriteMotionTokens
import com.tapasco.characters.ui.theme.InterdimensionalNeutral
import com.tapasco.characters.ui.theme.InterdimensionalRed
import com.tapasco.characters.ui.theme.characterStatusColor

@Composable
internal fun CharacterCard(
    character: Character,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(30.dp)
    val openDetailsLabel = stringResource(
        R.string.open_character_details,
        character.name,
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                onClick(
                    label = openDetailsLabel,
                    action = null,
                )
            },
        shape = cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
        ) {
            CharacterImage(
                character = character,
                modifier = Modifier.matchParentSize(),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CharacterStatusBadge(
                    status = character.status,
                )

                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteClick,
                )
            }

            CharacterDetails(
                character = character,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun CharacterImage(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        AsyncImage(
            model = character.imageUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.character_image_placeholder),
            error = painterResource(R.drawable.character_image_placeholder),
            fallback = painterResource(R.drawable.character_image_placeholder),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val favoriteStateDescription = stringResource(
        if (isFavorite) {
            R.string.favorite_state_selected
        } else {
            R.string.favorite_state_not_selected
        },
    )
    val favoriteActionDescription = stringResource(
        if (isFavorite) {
            R.string.remove_from_favorites
        } else {
            R.string.add_to_favorites
        },
    )
    val toggleScale = remember { Animatable(1f) }
    val hasFavoriteStateChanged = remember { mutableStateOf(false) }

    LaunchedEffect(isFavorite) {
        if (!hasFavoriteStateChanged.value) {
            hasFavoriteStateChanged.value = true
            return@LaunchedEffect
        }

        toggleScale.snapTo(
            if (isFavorite) {
                FavoriteMotionTokens.ADD_START_SCALE
            } else {
                FavoriteMotionTokens.REMOVE_START_SCALE
            },
        )
        toggleScale.animateTo(
            targetValue = if (isFavorite) {
                FavoriteMotionTokens.ADD_PEAK_SCALE
            } else {
                FavoriteMotionTokens.REMOVE_PEAK_SCALE
            },
            animationSpec = CharactersMotion.shortTween,
        )
        toggleScale.animateTo(
            targetValue = 1f,
            animationSpec = CharactersMotion.emphasizedSpring,
        )
    }

    FilledIconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        modifier = modifier
            .graphicsLayer {
                scaleX = toggleScale.value
                scaleY = toggleScale.value
            }
            .semantics {
                contentDescription = favoriteActionDescription
                stateDescription = favoriteStateDescription
            },
        colors = IconButtonDefaults.filledIconToggleButtonColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.88f),
            contentColor = MaterialTheme.colorScheme.onBackground,
            checkedContainerColor = InterdimensionalRed.copy(alpha = 0.92f),
            checkedContentColor = Color.White,
        ),
    ) {
        Crossfade(
            targetState = isFavorite,
            animationSpec = CharactersMotion.quickFade,
            label = "favorite icon",
        ) { favorite ->
            Icon(
                imageVector = if (favorite) {
                    Icons.Rounded.Favorite
                } else {
                    Icons.Rounded.FavoriteBorder
                },
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun CharacterStatusBadge(
    status: StatusCharacter,
    modifier: Modifier = Modifier,
) {
    val statusColor = characterStatusColor(status)
    val statusLabel = stringResource(status.labelRes)
    val statusDescription = stringResource(
        R.string.character_status_description,
        statusLabel,
    )

    Surface(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = statusDescription
        },
        shape = RoundedCornerShape(50),
        color = statusColor.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
    ) {
        Text(
            text = statusLabel.uppercase(),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            color = statusColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CharacterDetails(
    character: Character,
    modifier: Modifier = Modifier,
) {
    val glassShape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .clip(glassShape)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.32f),
                shape = glassShape,
            ),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    InterdimensionalNeutral.copy(alpha = 0.6f),
                ),
        )

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = character.name,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = stringResource(
                    R.string.character_species_origin,
                    character.species,
                    character.origin.name,
                ),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = stringResource(
                    R.string.character_last_seen,
                    character.location.name,
                ),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
