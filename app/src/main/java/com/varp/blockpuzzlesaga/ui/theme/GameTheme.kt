package com.varp.blockpuzzlesaga.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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

private val PrototypeScheme = lightColorScheme(
    primary = Color(0xFF3D5AFE),
    onPrimary = Color.White,
    secondary = Color(0xFF607D8B),
    background = Color(0xFFF4F6F8),
    surface = Color.White,
    onSurface = Color(0xFF17202A)
)

private val PrototypeGameColors = GameColors(
    boardBackground = Color(0xFFE7EBF0),
    boardLine = Color(0xFF7E8792),
    boardBlock = Color(0xFF44505C),
    boardBlockAlt = Color(0xFF5F6F7E),
    previewValid = Color(0x6634A853),
    previewInvalid = Color(0x66EA4335),
    trayBackground = Color(0xFFDCE2E8),
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

@Composable
fun BlockPuzzleSagaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalGameColors provides PrototypeGameColors) {
        MaterialTheme(
            colorScheme = PrototypeScheme,
            content = content
        )
    }
}
