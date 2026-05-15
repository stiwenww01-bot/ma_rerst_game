package com.varp.blockpuzzlesaga

import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.varp.blockpuzzlesaga.app.AppContainer
import com.varp.blockpuzzlesaga.ui.screens.game.GameScreen
import com.varp.blockpuzzlesaga.ui.screens.game.GameSoundEvent
import com.varp.blockpuzzlesaga.ui.screens.game.GameViewModel
import com.varp.blockpuzzlesaga.ui.screens.menu.MainMenuScreen
import com.varp.blockpuzzlesaga.ui.screens.records.RecordsScreen
import com.varp.blockpuzzlesaga.ui.screens.settings.SettingsScreen
import com.varp.blockpuzzlesaga.ui.screens.settings.SettingsViewModel
import com.varp.blockpuzzlesaga.ui.theme.BlockPuzzleSagaTheme
import com.varp.blockpuzzlesaga.ui.theme.GameBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            BlockPuzzleSagaApp()
        }
    }
}

@Composable
fun BlockPuzzleSagaApp() {
    val context = LocalContext.current
    val container = remember { AppContainer(context) }
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModel.Factory(
            gameRepository = container.gameRepository,
            recordsRepository = container.recordsRepository
        )
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            settingsRepository = container.settingsRepository
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    BlockPuzzleSagaTheme {
        GameSoundEffectPlayer(
            event = uiState.soundEvent,
            eventId = uiState.soundEventId,
            soundEnabled = settingsState.soundEnabled,
            effectsEnabled = settingsState.soundEffectsEnabled,
            volume = settingsState.sfxVolume
        )
        SpaceMusicPlayer(
            soundEnabled = settingsState.soundEnabled,
            musicEnabled = settingsState.musicEnabled,
            volume = settingsState.musicVolume
        )
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            GameBackground()
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                NavHost(
                    navController = navController,
                    startDestination = Route.Menu.path,
                    modifier = Modifier.systemBarsPadding()
                ) {
                    composable(Route.Menu.path) {
                        MainMenuScreen(
                            records = uiState.records,
                            onNewGame = {
                                viewModel.newGame()
                                navController.navigate(Route.Game.path)
                            },
                            onContinue = { navController.navigate(Route.Game.path) },
                            onRecords = { navController.navigate(Route.Records.path) },
                            onSettings = { navController.navigate(Route.Settings.path) }
                        )
                    }
                    composable(Route.Game.path) {
                        GameScreen(
                            uiState = uiState,
                            onBack = { navController.popBackStack(Route.Menu.path, inclusive = false) },
                            onSettings = { navController.navigate(Route.Settings.path) },
                            onSelectPiece = viewModel::selectPiece,
                            onRotate = viewModel::rotateSelectedPiece,
                            onPreview = viewModel::updateDragPreview,
                            onDrop = viewModel::dropPiece,
                            onCancelDrag = { viewModel.clearDragPreview() },
                            onRestart = viewModel::newGame
                        )
                    }
                    composable(Route.Records.path) {
                        LaunchedEffect(Unit) {
                            viewModel.refreshRecords()
                        }
                        RecordsScreen(
                            records = uiState.records,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Route.Settings.path) {
                        SettingsScreen(
                            uiState = settingsState,
                            onSoundEnabledChanged = settingsViewModel::setSoundEnabled,
                            onSoundEffectsEnabledChanged = settingsViewModel::setSoundEffectsEnabled,
                            onMusicEnabledChanged = settingsViewModel::setMusicEnabled,
                            onVibrationEnabledChanged = settingsViewModel::setVibrationEnabled,
                            onSfxVolumeChanged = settingsViewModel::setSfxVolume,
                            onMusicVolumeChanged = settingsViewModel::setMusicVolume,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameSoundEffectPlayer(
    event: GameSoundEvent?,
    eventId: Int,
    soundEnabled: Boolean,
    effectsEnabled: Boolean,
    volume: Float
) {
    val context = LocalContext.current
    var loadedCount by remember { mutableIntStateOf(0) }
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(5)
            .build()
    }
    val soundIds = remember(soundPool) {
        mapOf(
            GameSoundEvent.NewGame to soundPool.load(context, R.raw.sfx_new_game, 1),
            GameSoundEvent.Rotate to soundPool.load(context, R.raw.sfx_rotate, 1),
            GameSoundEvent.Place to soundPool.load(context, R.raw.sfx_place, 1),
            GameSoundEvent.Clear to soundPool.load(context, R.raw.sfx_clear, 1),
            GameSoundEvent.Bonus to soundPool.load(context, R.raw.sfx_bonus, 1),
            GameSoundEvent.Invalid to soundPool.load(context, R.raw.sfx_invalid, 1)
        )
    }
    DisposableEffect(soundPool) {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loadedCount += 1
        }
        onDispose { soundPool.release() }
    }
    LaunchedEffect(eventId) {
        val soundId = event?.let(soundIds::get) ?: return@LaunchedEffect
        val playVolume = volume.coerceIn(0f, 1f) * 0.78f
        if (eventId == 0 || !soundEnabled || !effectsEnabled || playVolume <= 0f || loadedCount == 0) {
            return@LaunchedEffect
        }
        soundPool.play(soundId, playVolume, playVolume, 1, 0, 1f)
    }
}

@Composable
private fun SpaceMusicPlayer(
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    volume: Float
) {
    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.music_space_ambient).apply {
            isLooping = true
        }
    }
    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
    LaunchedEffect(soundEnabled, musicEnabled, volume) {
        val musicVolume = if (soundEnabled && musicEnabled) volume.coerceIn(0f, 1f) * 0.42f else 0f
        mediaPlayer.setVolume(musicVolume, musicVolume)
        if (musicVolume > 0f && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        } else if (musicVolume <= 0f && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }
}

private enum class Route(val path: String) {
    Menu("menu"),
    Game("game"),
    Records("records"),
    Settings("settings")
}
