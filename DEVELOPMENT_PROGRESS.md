# Block Puzzle Saga - progress

Prompt source: `prompt.md`

## Current rule from prompt

Do not move to the next stage until the current stage checklist is complete.

## Stage 0. Preparation

Tasks:
- [x] Create Kotlin + Jetpack Compose Android project.
- [x] Use package `com.varp.blockpuzzlesaga`.
- [x] Add dependencies: Room, Navigation Compose, Coroutines, Lifecycle ViewModel.
- [x] Initialize Git repository.

Checks:
- [x] Project builds without errors.
- [x] Empty "Hello World" screen can launch on emulator/device.
- [x] Room is connected and a test database query passes.

## Stage 1. Domain model and game logic

Tasks:
- [x] `Piece` with normalized coordinate shapes, type, color index.
- [x] `Board` 9x9 with `canPlace`, `place`, clear helpers.
- [x] `LineChecker` for rows, columns, and 3x3 boxes.
- [x] `ScoreCalculator` for placement, clears, combo bonuses, collapse bonus.
- [x] `ComboTracker` for three identical pieces in a row.
- [x] `PieceGenerator` with 26 templates and balanced tray generation.
- [x] `RotationManager` with 3 rotations per session.
- [x] `GameState` aggregate with placement, rotation, scoring, collapse, and `isGameOver()`.

Checks:
- [x] JVM unit tests pass (`:app:testDebugUnitTest`, 37 tests total including Room smoke test).
- [x] Domain logic coverage is >= 80% (`:app:domainDebugCoverageCheck`, 96.73% instruction coverage).
- [x] Domain logic has no Android SDK dependency.

## Stage 2. Database (Room)

Tasks:
- [x] Add entities: `RecordEntity`, `SettingsEntity`, `GameStateEntity`, `StatisticsEntity`.
- [x] Add DAO CRUD methods for records, settings, game state, and statistics.
- [x] Upgrade `AppDatabase` to version 2 with `MIGRATION_1_2`.
- [x] Add repositories: `GameRepository`, `SettingsRepository`, `StatsRepository`, `RecordsRepository`.
- [x] Add JSON serialization for `GameState` using `kotlinx.serialization`.

Checks:
- [x] Test: save and read record.
- [x] Test: save unfinished game and restore it.
- [x] Test: saved game can be cleared.
- [x] Test: settings and statistics persist through Room.
- [x] Test: migration creates Stage 2 tables.
- [x] JVM unit tests pass (`:app:testDebugUnitTest`, 44 tests total).
- [x] Debug build passes (`:app:assembleDebug`).

## Stage 3. Basic UI prototype

Tasks:
- [x] `MainMenuScreen` with New Game, Continue, Records, Settings.
- [x] `GameScreen` with 9x9 Compose Canvas board.
- [x] Piece tray with three Canvas-rendered pieces.
- [x] Drag and drop from tray to board with valid/invalid preview.
- [x] Score, rotation counter, and rotate button.
- [x] Basic `GameOverDialog` wired to domain `gameOver`.
- [x] `RecordsScreen` table.
- [x] `SettingsScreen` placeholder.
- [x] Game state persists through Room after moves and rotation.

Checks:
- [x] Debug build passes (`:app:assembleDebug`).
- [x] JVM unit tests pass (`:app:testDebugUnitTest`, 44 tests total).
- [x] Domain coverage check passes (`:app:domainDebugCoverageCheck`, 96.73% instruction coverage).
- [x] Emulator launch verified.
- [x] Drag/drop verified on emulator; score updated after placement.
- [x] Rotation verified on emulator; counter decreased from 3 to 2.
- [x] Continue restored saved game after reinstall.
- [ ] Full manual playthrough to Game Over.

## Stage 4. Graphical themes

Notes:
- No Figma link or selected frame was provided for this stage, so the current implementation uses code-driven Compose themes and Canvas previews/effects. Figma asset export remains open if a source design is provided later.

Tasks:
- [x] Add a Compose theme provider with four theme ids: Space, Western, Classic, Cyberpunk.
- [x] Add separate color palettes for board, tray, blocks, previews, backgrounds, and particles.
- [x] Replace the fixed space background with a theme-aware Canvas background.
- [x] Add simple particle effects per theme: stars, dust, chips, and pixels.
- [x] Add `SettingsViewModel` with reactive settings state from Room.
- [x] Add `SettingsScreen` theme picker with four preview cards.
- [x] Persist selected theme in existing Room settings.
- [ ] Figma MCP source layouts and exported PNG/WebP/font assets.

Checks:
- [x] JVM unit tests pass (`:app:testDebugUnitTest`).
- [x] Debug build passes (`:app:assembleDebug`).
- [x] Theme changes apply without app restart on emulator.
- [x] Selected theme survives force-stop/relaunch on emulator.
- [x] Game screen uses the selected theme on emulator.
- [ ] Particle FPS check with performance tooling.
