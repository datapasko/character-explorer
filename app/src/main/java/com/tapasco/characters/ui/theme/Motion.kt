package com.tapasco.characters.ui.theme

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntSize

internal object CharactersMotion {
    private const val QUICK_DURATION_MILLIS = 100
    private const val SHORT_DURATION_MILLIS = 110
    private const val HEADER_RESIZE_DURATION_MILLIS = 200
    private const val SKELETON_PULSE_DURATION_MILLIS = 800

    val quickFade: TweenSpec<Float> = tween(
        durationMillis = QUICK_DURATION_MILLIS,
    )

    val shortTween: TweenSpec<Float> = tween(
        durationMillis = SHORT_DURATION_MILLIS,
    )

    val emphasizedSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.6f,
        stiffness = 650f,
    )

    val headerResize: TweenSpec<IntSize> = tween(
        durationMillis = HEADER_RESIZE_DURATION_MILLIS,
    )

    val skeletonPulse: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis = SKELETON_PULSE_DURATION_MILLIS),
        repeatMode = RepeatMode.Reverse,
    )
}

internal object FavoriteMotionTokens {
    const val ADD_START_SCALE = 0.88f
    const val ADD_PEAK_SCALE = 1.18f
    const val REMOVE_START_SCALE = 1.08f
    const val REMOVE_PEAK_SCALE = 0.88f
}

internal object LoadingMotionTokens {
    const val SKELETON_MIN_ALPHA = 0.55f
    const val SKELETON_MAX_ALPHA = 0.9f
}
