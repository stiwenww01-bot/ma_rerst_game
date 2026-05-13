package com.varp.blockpuzzlesaga.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

@Immutable
data class GameColors(
    val boardBackground: Color,
    val boardLine: Color,
    val boardBlock: Color,
    val boardBlockAlt: Color,
    val previewValid: Color,
    val previewInvalid: Color,
    val trayBackground: Color,
    val piecePalette: List<Color>
)

val LocalGameColors = staticCompositionLocalOf {
    GameColors(
        boardBackground = Color.Unspecified,
        boardLine = Color.Unspecified,
        boardBlock = Color.Unspecified,
        boardBlockAlt = Color.Unspecified,
        previewValid = Color.Unspecified,
        previewInvalid = Color.Unspecified,
        trayBackground = Color.Unspecified,
        piecePalette = emptyList()
    )
}

private val PrototypeScheme = darkColorScheme(
    primary = Color(0xFF64B5FF),
    onPrimary = Color.White,
    secondary = Color(0xFFE040FB),
    background = Color(0xFF020817),
    surface = Color(0xCC081A38),
    onSurface = Color(0xFFEAF5FF)
)

private val PrototypeGameColors = GameColors(
    boardBackground = Color(0xAA03152E),
    boardLine = Color(0xFF39A7FF),
    boardBlock = Color(0xFF203C5F),
    boardBlockAlt = Color(0xFF2E587E),
    previewValid = Color(0xAA7B2CFF),
    previewInvalid = Color(0xAAFF2C6D),
    trayBackground = Color(0x66112C52),
    piecePalette = listOf(
        Color(0xFF9C27FF),
        Color(0xFF5D5CFF),
        Color(0xFF00D4FF),
        Color(0xFFE040FB),
        Color(0xFFFF2D75),
        Color(0xFF7C4DFF),
        Color(0xFF40C4FF),
        Color(0xFFB388FF),
        Color(0xFF18FFFF)
    )
)

@Composable
fun BlockPuzzleSagaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalGameColors provides PrototypeGameColors) {
        MaterialTheme(
            colorScheme = PrototypeScheme,
            content = content
        )
    }
}

@Composable
fun SpaceBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF01040D),
                    Color(0xFF061231),
                    Color(0xFF080519)
                )
            ),
            size = size
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xCC1E88FF), Color.Transparent),
                center = Offset(size.width * 0.08f, size.height * 0.02f),
                radius = size.width * 0.34f
            ),
            radius = size.width * 0.34f,
            center = Offset(size.width * 0.08f, size.height * 0.02f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xAA8E24FF), Color.Transparent),
                center = Offset(size.width * 0.98f, size.height * 0.06f),
                radius = size.width * 0.22f
            ),
            radius = size.width * 0.22f,
            center = Offset(size.width * 0.98f, size.height * 0.06f)
        )
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x662A7FFF), Color.Transparent),
                center = Offset(size.width * 0.48f, size.height * 0.36f),
                radius = size.width * 0.8f
            ),
            topLeft = Offset(-size.width * 0.2f, size.height * 0.18f),
            size = Size(size.width * 1.4f, size.height * 0.45f)
        )

        repeat(90) { index ->
            val x = ((index * 73) % 997) / 997f * size.width
            val y = ((index * 151) % 991) / 991f * size.height
            val radius = 1f + (index % 4) * 0.45f
            val alpha = 0.35f + (sin(index.toFloat()) + 1f) * 0.25f
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}
