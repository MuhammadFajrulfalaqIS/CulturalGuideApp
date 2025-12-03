// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Terapkan Plugin Hilt
    id("com.google.dagger.hilt.android") version "2.57.1" apply false

    // Terapkan Plugin Google Services (untuk Maps)
//    alias(libs.plugins.googleServices) apply false

    // ⭐ PASTI DI SINI: Terapkan Plugin KSP
//    alias(libs.plugins.ksp) apply false

    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false

//    id("com.google.gms.google-services") version "4.4.4" apply false
}

// Configure JVM toolchain for consistent compilation across all tasks
allprojects {
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}
