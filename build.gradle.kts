// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Terapkan Plugin Hilt
    id("com.google.dagger.hilt.android") version "2.57.1" apply false

    // Terapkan Plugin Google Services (untuk Maps)
    alias(libs.plugins.googleServices) apply false

    // ⭐ PASTI DI SINI: Terapkan Plugin KSP
    alias(libs.plugins.ksp) apply false
}