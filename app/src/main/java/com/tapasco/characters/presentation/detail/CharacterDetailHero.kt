package com.tapasco.characters.presentation.detail

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.ui.theme.CharacterDetailMotionTokens
import com.tapasco.characters.ui.theme.CharactersMotion
import com.tapasco.characters.ui.theme.InterdimensionalGreen
import com.tapasco.characters.ui.theme.InterdimensionalMagenta
import com.tapasco.characters.ui.theme.InterdimensionalNeutral
import com.tapasco.characters.ui.theme.characterStatusColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun CharacterDetailHero(
    character: Character,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val portraitSize =
            maxWidth * CharacterDetailMotionTokens.PORTRAIT_SIZE_FRACTION
        val portraitDescription = stringResource(
            R.string.character_portrait_description,
            character.name,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(maxWidth),
            contentAlignment = Alignment.Center,
        ) {
            OrbitingParticles(modifier = Modifier.fillMaxSize())

            AsyncImage(
                model = character.imageUrl,
                contentDescription = portraitDescription,
                modifier = Modifier
                    .size(portraitSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 3.dp,
                        color = InterdimensionalGreen,
                        shape = CircleShape,
                    ),
                contentScale = ContentScale.Crop,
            )

            DetailBadge(
                label = stringResource(R.string.character_id_label),
                value = stringResource(R.string.character_code, character.id),
                accentColor = InterdimensionalMagenta,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 4.dp, y = 44.dp),
            )

            DetailBadge(
                label = stringResource(R.string.character_status_label),
                value = character.status.uppercase(),
                accentColor = characterStatusColor(character.status),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-4).dp, y = (-44).dp),
            )
        }
    }
}

@Composable
private fun OrbitingParticles(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "character orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = CharacterDetailMotionTokens.FULL_ROTATION_DEGREES,
        animationSpec = CharactersMotion.orbitRotation,
        label = "orbit rotation",
    )

    Canvas(modifier = modifier) {
        val radius =
            size.minDimension * CharacterDetailMotionTokens.ORBIT_RADIUS_FRACTION
        val strokeWidth = 2.dp.toPx()
        val orbitColor = InterdimensionalGreen

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    orbitColor.copy(alpha = 0.18f),
                    Color.Transparent,
                ),
                center = center,
                radius = radius * 1.18f,
            ),
            radius = radius * 1.18f,
        )
        drawCircle(
            color = orbitColor.copy(alpha = 0.55f),
            radius = radius,
            style = Stroke(width = strokeWidth),
        )
        drawCircle(
            color = orbitColor.copy(alpha = 0.38f),
            radius = radius * 1.09f,
            style = Stroke(
                width = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(8.dp.toPx(), 10.dp.toPx()),
                    phase = rotation * CharacterDetailMotionTokens.DASH_PHASE_MULTIPLIER,
                ),
            ),
        )

        repeat(CharacterDetailMotionTokens.ORBIT_PARTICLE_COUNT) { index ->
            val angleDegrees =
                rotation +
                    (
                        CharacterDetailMotionTokens.FULL_ROTATION_DEGREES /
                            CharacterDetailMotionTokens.ORBIT_PARTICLE_COUNT
                        ) * index
            val angleRadians =
                angleDegrees *
                    PI.toFloat() /
                    CharacterDetailMotionTokens.HALF_ROTATION_DEGREES
            val particleOrbitRadius =
                radius *
                    (
                        1f +
                            ((index % 3) - 1) *
                            CharacterDetailMotionTokens.PARTICLE_RADIUS_VARIATION
                        )
            val particleCenter = Offset(
                x = center.x + cos(angleRadians) * particleOrbitRadius,
                y = center.y + sin(angleRadians) * particleOrbitRadius,
            )

            drawCircle(
                color = orbitColor.copy(alpha = 0.55f + (index % 3) * 0.15f),
                radius = (2 + index % 3).dp.toPx(),
                center = particleCenter,
            )
        }
    }
}

@Composable
private fun DetailBadge(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "$label: $value"
        },
        shape = RoundedCornerShape(10.dp),
        color = InterdimensionalNeutral.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)),
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(
                text = label.uppercase(),
                color = accentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                color = accentColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}
