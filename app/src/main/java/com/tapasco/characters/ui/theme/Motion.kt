package com.tapasco.characters.ui.theme

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
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
    private const val ORBIT_ROTATION_DURATION_MILLIS = 12_000

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

    val orbitRotation: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = ORBIT_ROTATION_DURATION_MILLIS,
            easing = LinearEasing,
        ),
        repeatMode = RepeatMode.Restart,
    )
}

internal object FavoriteMotionTokens {
    const val ADD_START_SCALE = 0.88f
    const val ADD_PEAK_SCALE = 1.18f
    const val REMOVE_START_SCALE = 1.08f
    const val REMOVE_PEAK_SCALE = 0.88f
}

internal object CharacterDetailMotionTokens {
    const val PORTRAIT_SIZE_FRACTION = 0.68f
    const val ORBIT_RADIUS_FRACTION = 0.38f
    const val PARTICLE_RADIUS_VARIATION = 0.018f
    const val ORBIT_PARTICLE_COUNT = 14
    const val FULL_ROTATION_DEGREES = 360f
    const val HALF_ROTATION_DEGREES = 180f
    const val DASH_PHASE_MULTIPLIER = 0.12f
}

internal object LoadingMotionTokens {
    const val SKELETON_MIN_ALPHA = 0.55f
    const val SKELETON_MAX_ALPHA = 0.9f
}
