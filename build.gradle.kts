// Top-level build file: declares plugin versions once so app/build.gradle.kts
// can apply them without repeating version numbers.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false
    // Applied in app/build.gradle.kts starting Fase 2, once google-services.json exists.
    alias(libs.plugins.google.services) apply false
}
