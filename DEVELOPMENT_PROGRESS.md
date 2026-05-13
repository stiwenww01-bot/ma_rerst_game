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

Status: ready to start.
