plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation(project(":markard"))
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.material3)
        }
    }
}

compose.desktop { application { mainClass = "dev.tireless.markard.sample.MainKt" } }
