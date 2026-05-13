import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("jacoco")
}

android {
    namespace = "com.varp.blockpuzzlesaga"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.varp.blockpuzzlesaga"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("androidx.room:room-testing:2.7.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

afterEvaluate {
    tasks.named<Test>("testDebugUnitTest") {
        classpath = classpath.plus(files(layout.buildDirectory.dir("tmp/kotlin-classes/debugUnitTest")))
    }

    val domainClassFiles = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        include("com/varp/blockpuzzlesaga/domain/**/*.class")
        exclude("**/*\$Companion.class")
    }
    val domainSourceFiles = files("src/main/java/com/varp/blockpuzzlesaga/domain")
    val executionFiles = fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    }

    tasks.register<JacocoReport>("domainDebugCoverageReport") {
        dependsOn("testDebugUnitTest")
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
        classDirectories.setFrom(domainClassFiles)
        sourceDirectories.setFrom(domainSourceFiles)
        executionData.setFrom(executionFiles)
    }

    tasks.register<JacocoCoverageVerification>("domainDebugCoverageCheck") {
        dependsOn("domainDebugCoverageReport")
        classDirectories.setFrom(domainClassFiles)
        sourceDirectories.setFrom(domainSourceFiles)
        executionData.setFrom(executionFiles)
        violationRules {
            rule {
                limit {
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }
}
