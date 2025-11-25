plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Tambahkan Hilt dan KSP
//    kotlin("kapt") // Required for Kotlin and KAPT
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") // KSP untuk annotation processing (Hilt & Room)
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cultural_navigation_papb"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cultural_navigation_papb"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
    }
}

dependencies {
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Maps
    implementation(libs.bundles.maps)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Image Loading
    implementation(libs.coil.compose)

    // JSON Parsing
    implementation(libs.gson)

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Core Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}