package com.tapasco.characters.presentation.characters

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.tapasco.characters.ui.theme.CharactersMotion
import com.tapasco.characters.ui.theme.LoadingMotionTokens

@Composable
internal fun CharacterSkeletonList(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val skeletonAlpha = rememberSkeletonAlpha()

    LazyColumn(
        modifier = modifier.clearAndSetSemantics {
            this.contentDescription = contentDescription
            liveRegion = LiveRegionMode.Polite
        },
        userScrollEnabled = false,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = INITIAL_SKELETON_COUNT,
            key = { index -> "$INITIAL_SKELETON_KEY_PREFIX$index" },
        ) {
            CharacterCardSkeleton(alpha = skeletonAlpha)
        }
    }
}

@Composable
internal fun CharacterCardSkeleton(
    modifier: Modifier = Modifier,
    alpha: Float = rememberSkeletonAlpha(),
) {
    val cardShape = RoundedCornerShape(30.dp)
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.22f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .alpha(alpha),
        shape = cardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(88.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(50))
                        .background(placeholderColor),
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(placeholderColor),
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(cardShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SkeletonLine(
                    widthFraction = 0.72f,
                    height = 24.dp,
                    color = placeholderColor,
                )
                SkeletonLine(
                    widthFraction = 0.92f,
                    height = 16.dp,
                    color = placeholderColor,
                )
                SkeletonLine(
                    widthFraction = 0.64f,
                    height = 14.dp,
                    color = placeholderColor,
                )
            }
        }
    }
}

@Composable
private fun SkeletonLine(
    widthFraction: Float,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(color),
    )
}

@Composable
private fun rememberSkeletonAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "characterSkeleton")
    val alpha by transition.animateFloat(
        initialValue = LoadingMotionTokens.SKELETON_MIN_ALPHA,
        targetValue = LoadingMotionTokens.SKELETON_MAX_ALPHA,
        animationSpec = CharactersMotion.skeletonPulse,
        label = "characterSkeletonAlpha",
    )

    return alpha
}

private const val INITIAL_SKELETON_COUNT = 3
private const val INITIAL_SKELETON_KEY_PREFIX = "initial_skeleton_"
