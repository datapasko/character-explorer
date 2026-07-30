package com.tapasco.characters.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tapasco.characters.ui.theme.CharacterDetailMotionTokens
import com.tapasco.characters.ui.theme.rememberSkeletonAlpha

@Composable
internal fun CharacterDetailSkeleton(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val skeletonAlpha = rememberSkeletonAlpha()
    val placeholderColor =
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.22f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .alpha(skeletonAlpha)
            .clearAndSetSemantics {
                this.contentDescription = contentDescription
                liveRegion = LiveRegionMode.Polite
            },
        userScrollEnabled = false,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "detail_skeleton_hero") {
            CharacterHeroSkeleton(placeholderColor = placeholderColor)
        }

        item(key = "detail_skeleton_identity") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SkeletonBlock(
                    width = 210.dp,
                    height = 30.dp,
                    color = placeholderColor,
                )
                SkeletonBlock(
                    width = 132.dp,
                    height = 18.dp,
                    color = placeholderColor,
                )
            }
        }

        items(
            count = INFO_CARD_SKELETON_COUNT,
            key = { index -> "detail_skeleton_info_$index" },
        ) {
            InfoCardSkeleton(placeholderColor = placeholderColor)
        }

        item(key = "detail_skeleton_episodes") {
            EpisodesSkeleton(placeholderColor = placeholderColor)
        }
    }
}

@Composable
private fun CharacterHeroSkeleton(
    placeholderColor: Color,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val heroSize = maxWidth
        val portraitSize =
            heroSize * CharacterDetailMotionTokens.PORTRAIT_SIZE_FRACTION

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(heroSize),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(
                        heroSize *
                            CharacterDetailMotionTokens.ORBIT_RADIUS_FRACTION *
                            ORBIT_DIAMETER_MULTIPLIER,
                    )
                    .border(
                        width = 2.dp,
                        color = placeholderColor,
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .size(portraitSize)
                    .clip(CircleShape)
                    .background(placeholderColor),
            )
            SkeletonBlock(
                width = 92.dp,
                height = 62.dp,
                color = placeholderColor,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 4.dp, y = 44.dp),
            )
            SkeletonBlock(
                width = 104.dp,
                height = 62.dp,
                color = placeholderColor,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-4).dp, y = (-44).dp),
            )
        }
    }
}

@Composable
private fun InfoCardSkeleton(
    placeholderColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SkeletonBlock(
                width = 72.dp,
                height = 12.dp,
                color = placeholderColor,
            )
            SkeletonBlock(
                width = 176.dp,
                height = 18.dp,
                color = placeholderColor,
            )
        }
    }
}

@Composable
private fun EpisodesSkeleton(
    placeholderColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SkeletonBlock(
            width = 104.dp,
            height = 24.dp,
            color = placeholderColor,
        )
        SkeletonBlock(
            width = 88.dp,
            height = 14.dp,
            color = placeholderColor,
        )
        LazyRow(
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = EPISODE_CARD_SKELETON_COUNT,
                key = { index -> "detail_skeleton_episode_$index" },
            ) {
                Surface(
                    modifier = Modifier
                        .width(236.dp)
                        .height(132.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SkeletonBlock(
                            width = 64.dp,
                            height = 14.dp,
                            color = placeholderColor,
                        )
                        SkeletonBlock(
                            width = 184.dp,
                            height = 20.dp,
                            color = placeholderColor,
                        )
                        SkeletonBlock(
                            width = 112.dp,
                            height = 14.dp,
                            color = placeholderColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonBlock(
    width: Dp,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(50),
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(color),
    )
}

private const val INFO_CARD_SKELETON_COUNT = 2
private const val EPISODE_CARD_SKELETON_COUNT = 2
private const val ORBIT_DIAMETER_MULTIPLIER = 2f
