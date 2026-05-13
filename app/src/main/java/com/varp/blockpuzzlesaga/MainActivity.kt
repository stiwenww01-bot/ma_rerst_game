package com.varp.blockpuzzlesaga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.varp.blockpuzzlesaga.app.AppContainer
import com.varp.blockpuzzlesaga.ui.screens.game.GameScreen
import com.varp.blockpuzzlesaga.ui.screens.game.GameViewModel
import com.varp.blockpuzzlesaga.ui.screens.menu.MainMenuScreen
import com.varp.blockpuzzlesaga.ui.screens.records.RecordsScreen
import com.varp.blockpuzzlesaga.ui.screens.settings.SettingsScreen
import com.varp.blockpuzzlesaga.ui.theme.BlockPuzzleSagaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    BlockPuzzleSagaTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
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
                    startDestination = Route.Menu.path
                ) {
                    composable(Route.Menu.path) {
                        MainMenuScreen(
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
                            onSelectPiece = viewModel::selectPiece,
                            onRotate = viewModel::rotateSelectedPiece,
                            onPreview = viewModel::updateDragPreview,
                            onDrop = viewModel::dropPiece,
                            onCancelDrag = viewModel::clearDragPreview,
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
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

private enum class Route(val path: String) {
    Menu("menu"),
    Game("game"),
    Records("records"),
    Settings("settings")
}
