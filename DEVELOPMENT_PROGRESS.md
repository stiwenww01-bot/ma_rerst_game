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

Status: ready to start.
