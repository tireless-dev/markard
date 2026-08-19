@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android {
    namespace = "dev.tireless.markard"
    compileSdk = 37
    minSdk = 24
  }
  jvm()
//  iosX64()
  iosArm64()
  iosSimulatorArm64()
  wasmJs { browser() }

  targets.withType<KotlinNativeTarget>().configureEach {
    binaries.framework { baseName = "Markard"; isStatic = true }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.ui)
      implementation(libs.compose.material3)
    }
    commonTest.dependencies { implementation(kotlin("test")) }
  }
}

// The initial project validates Wasm compilation; browser test execution is deferred
// until the library has platform-specific test coverage.
tasks.matching { it.name == "compileTestDevelopmentExecutableKotlinWasmJs" }.configureEach {
  enabled = false
}
