plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
    id("com.google.devtools.ksp") version "2.1.20-2.0.0" apply false
}

val externalBuildRoot = file("D:/Coding/Game1BuildOut")
layout.buildDirectory.set(externalBuildRoot.resolve("root"))

subprojects {
    layout.buildDirectory.set(externalBuildRoot.resolve(name))
}
