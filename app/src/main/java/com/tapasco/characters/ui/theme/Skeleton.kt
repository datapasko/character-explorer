package com.tapasco.characters.ui.theme

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
internal fun rememberSkeletonAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = LoadingMotionTokens.SKELETON_MIN_ALPHA,
        targetValue = LoadingMotionTokens.SKELETON_MAX_ALPHA,
        animationSpec = CharactersMotion.skeletonPulse,
        label = "skeletonAlpha",
    )

    return alpha
}
