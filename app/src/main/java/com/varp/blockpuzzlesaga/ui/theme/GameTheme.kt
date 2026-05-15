package com.varp.blockpuzzlesaga.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.varp.blockpuzzlesaga.R
import kotlin.math.sin

enum class GameThemeId(
    val storageKey: String,
    val displayName: String
) {
    Space("space", "Космос"),
    Western("western", "Вестерн"),
    Classic("classic", "Классика"),
    Cyberpunk("cyberpunk", "Киберпанк");

    companion object {
        val default = Space

        fun fromKey(key: String?): GameThemeId {
            return values().firstOrNull { it.storageKey == key } ?: default
        }
    }
}

enum class ParticleStyle {
    Stars,
    Dust,
    Chips,
    Pixels
}

@Immutable
data class GameColors(
    val themeId: GameThemeId,
    val boardBackground: Color,
    val boardLine: Color,
    val boardBlock: Color,
    val boardBlockAlt: Color,
    val previewValid: Color,
    val previewInvalid: Color,
    val clearHighlight: Color,
    val trayBackground: Color,
    val panelBackground: Color,
    val backgroundTop: Color,
    val backgroundMiddle: Color,
    val backgroundBottom: Color,
    val glowPrimary: Color,
    val glowSecondary: Color,
    val particleColor: Color,
    val particleStyle: ParticleStyle,
    val piecePalette: List<Color>
)

val LocalGameColors = staticCompositionLocalOf { SpaceGameColors }

private val SpaceGameColors = GameColors(
    themeId = GameThemeId.Space,
    boardBackground = Color(0xAA03152E),
    boardLine = Color(0xFF39A7FF),
    boardBlock = Color(0xFF203C5F),
    boardBlockAlt = Color(0xFF2E587E),
    previewValid = Color(0xAA7B2CFF),
    previewInvalid = Color(0xAAFF2C6D),
    clearHighlight = Color(0xFF8FFBFF),
    trayBackground = Color(0x66112C52),
    panelBackground = Color(0xAA082349),
    backgroundTop = Color(0xFF01040D),
    backgroundMiddle = Color(0xFF061231),
    backgroundBottom = Color(0xFF080519),
    glowPrimary = Color(0xCC1E88FF),
    glowSecondary = Color(0xAA8E24FF),
    particleColor = Color.White,
    particleStyle = ParticleStyle.Stars,
    piecePalette = listOf(
        Color(0xFF22E6F0),
        Color(0xFF8A45F7),
        Color(0xFFFF2D75)
    )
)

private val WesternGameColors = GameColors(
    themeId = GameThemeId.Western,
    boardBackground = Color(0xCC27150A),
    boardLine = Color(0xFFFFC15E),
    boardBlock = Color(0xFF7A3D22),
    boardBlockAlt = Color(0xFF9D5A2E),
    previewValid = Color(0xAAE7B85B),
    previewInvalid = Color(0xAAE24A38),
    clearHighlight = Color(0xFFFFE2A0),
    trayBackground = Color(0x77301811),
    panelBackground = Color(0xB23D2114),
    backgroundTop = Color(0xFF160907),
    backgroundMiddle = Color(0xFF40200F),
    backgroundBottom = Color(0xFF0F0705),
    glowPrimary = Color(0xB2FF9E3D),
    glowSecondary = Color(0x99F3D07A),
    particleColor = Color(0xFFFFD08A),
    particleStyle = ParticleStyle.Dust,
    piecePalette = listOf(
        Color(0xFFFFC15E),
        Color(0xFFE4572E),
        Color(0xFF7CCBA2),
        Color(0xFFD7814A),
        Color(0xFFF2D36B),
        Color(0xFFA65F35),
        Color(0xFFFF8A3D),
        Color(0xFF6BAF92),
        Color(0xFFC94F28)
    )
)

private val ClassicGameColors = GameColors(
    themeId = GameThemeId.Classic,
    boardBackground = Color(0xFFE7EBF0),
    boardLine = Color(0xFF7E8792),
    boardBlock = Color(0xFF44505C),
    boardBlockAlt = Color(0xFF5F6F7E),
    previewValid = Color(0x6634A853),
    previewInvalid = Color(0x66EA4335),
    clearHighlight = Color(0xFFFFFFFF),
    trayBackground = Color(0xFFDCE2E8),
    panelBackground = Color(0xFFE9EEF4),
    backgroundTop = Color(0xFFEFF4F8),
    backgroundMiddle = Color(0xFFDDE5EC),
    backgroundBottom = Color(0xFFF9FBFC),
    glowPrimary = Color(0x668EA0AE),
    glowSecondary = Color(0x55FFFFFF),
    particleColor = Color(0xFF5E6A75),
    particleStyle = ParticleStyle.Chips,
    piecePalette = listOf(
        Color(0xFF3D5AFE),
        Color(0xFF00897B),
        Color(0xFF7B1FA2),
        Color(0xFFD81B60),
        Color(0xFFEF6C00),
        Color(0xFF546E7A),
        Color(0xFF3949AB),
        Color(0xFF2E7D32),
        Color(0xFF6D4C41)
    )
)

private val CyberpunkGameColors = GameColors(
    themeId = GameThemeId.Cyberpunk,
    boardBackground = Color(0xCC080513),
    boardLine = Color(0xFFFFEA00),
    boardBlock = Color(0xFF14264E),
    boardBlockAlt = Color(0xFF34205C),
    previewValid = Color(0xAA00F5FF),
    previewInvalid = Color(0xAAFF1B8D),
    clearHighlight = Color(0xFFFFEA00),
    trayBackground = Color(0x77200646),
    panelBackground = Color(0xAA130A2F),
    backgroundTop = Color(0xFF06020D),
    backgroundMiddle = Color(0xFF210B45),
    backgroundBottom = Color(0xFF04020A),
    glowPrimary = Color(0xCCFFEA00),
    glowSecondary = Color(0xCCFF1B8D),
    particleColor = Color(0xFF00F5FF),
    particleStyle = ParticleStyle.Pixels,
    piecePalette = listOf(
        Color(0xFFFFEA00),
        Color(0xFFFF1B8D),
        Color(0xFF00F5FF),
        Color(0xFF7B2CFF),
        Color(0xFFFF5C00),
        Color(0xFF00FF85),
        Color(0xFFFF3DF2),
        Color(0xFF35A7FF),
        Color(0xFFFFD166)
    )
)

val GameThemePalettes = listOf(
    SpaceGameColors,
    WesternGameColors,
    ClassicGameColors,
    CyberpunkGameColors
)

private val AppTypography = Typography()

private fun colorsFor(themeKey: String?): GameColors {
    val themeId = GameThemeId.fromKey(themeKey)
    return GameThemePalettes.first { it.themeId == themeId }
}

@Composable
fun BlockPuzzleSagaTheme(
    selectedThemeKey: String = GameThemeId.default.storageKey,
    content: @Composable () -> Unit
) {
    val gameColors = colorsFor(selectedThemeKey)
    val scheme = darkColorScheme(
        primary = gameColors.boardLine,
        onPrimary = Color.White,
        secondary = gameColors.glowSecondary,
        background = gameColors.backgroundBottom,
        surface = gameColors.panelBackground,
        onSurface = if (gameColors.themeId == GameThemeId.Classic) Color(0xFF17202A) else Color(0xFFEAF5FF),
        onBackground = if (gameColors.themeId == GameThemeId.Classic) Color(0xFF17202A) else Color(0xFFEAF5FF)
    )

    CompositionLocalProvider(LocalGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = AppTypography,
            content = content
        )
    }
}

@Composable
fun GameBackground(modifier: Modifier = Modifier) {
    val colors = LocalGameColors.current
    if (colors.themeId == GameThemeId.Space) {
        Image(
            painter = painterResource(R.drawable.space_bg_game),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        return
    }

    val transition = rememberInfiniteTransition(label = "theme-particles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle-phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colors.backgroundTop, colors.backgroundMiddle, colors.backgroundBottom)
            ),
            size = size
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.glowPrimary, Color.Transparent),
                center = Offset(size.width * 0.08f, size.height * 0.04f),
                radius = size.width * 0.36f
            ),
            radius = size.width * 0.36f,
            center = Offset(size.width * 0.08f, size.height * 0.04f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.glowSecondary, Color.Transparent),
                center = Offset(size.width * 0.96f, size.height * 0.08f),
                radius = size.width * 0.24f
            ),
            radius = size.width * 0.24f,
            center = Offset(size.width * 0.96f, size.height * 0.08f)
        )
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(colors.glowPrimary.copy(alpha = 0.34f), Color.Transparent),
                center = Offset(size.width * 0.48f, size.height * 0.36f),
                radius = size.width * 0.78f
            ),
            topLeft = Offset(-size.width * 0.2f, size.height * 0.18f),
            size = Size(size.width * 1.4f, size.height * 0.45f)
        )

        repeat(80) { index ->
            val baseX = ((index * 73) % 997) / 997f * size.width
            val baseY = ((index * 151) % 991) / 991f * size.height
            val drift = (phase * size.height * 0.08f + index * 3f) % (size.height * 0.12f)
            val x = (baseX + sin(phase * 6.28f + index) * 8f).coerceIn(0f, size.width)
            val y = (baseY + drift) % size.height
            val alpha = 0.28f + (sin(index + phase * 6.28f) + 1f) * 0.22f

            when (colors.particleStyle) {
                ParticleStyle.Stars -> drawCircle(
                    color = colors.particleColor.copy(alpha = alpha),
                    radius = 1f + (index % 4) * 0.45f,
                    center = Offset(x, y)
                )

                ParticleStyle.Dust -> drawCircle(
                    color = colors.particleColor.copy(alpha = alpha * 0.55f),
                    radius = 1.8f + (index % 5),
                    center = Offset(x, y)
                )

                ParticleStyle.Chips -> drawRect(
                    color = colors.particleColor.copy(alpha = alpha * 0.26f),
                    topLeft = Offset(x, y),
                    size = Size(3f + index % 5, 1.5f + index % 3)
                )

                ParticleStyle.Pixels -> drawRect(
                    color = colors.particleColor.copy(alpha = alpha * 0.62f),
                    topLeft = Offset(x, y),
                    size = Size(3f + index % 4, 3f + index % 4)
                )
            }
        }
    }
}
