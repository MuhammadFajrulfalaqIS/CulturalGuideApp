import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
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

        // Load API keys from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        // BuildConfig fields for API keys
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${localProperties.getProperty("GOOGLE_MAPS_API_KEY") ?: ""}\"")
        buildConfigField("String", "GOOGLE_DIRECTIONS_API_KEY", "\"${localProperties.getProperty("GOOGLE_DIRECTIONS_API_KEY") ?: ""}\"")

        // ✅ NEW: Cloudinary credentials untuk upload gambar review
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${localProperties.getProperty("CLOUDINARY_CLOUD_NAME") ?: ""}\"")
        buildConfigField("String", "CLOUDINARY_UPLOAD_PRESET", "\"${localProperties.getProperty("CLOUDINARY_UPLOAD_PRESET") ?: ""}\"")
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

    // ✅ IMPORTANT: Enable BuildConfig
    buildFeatures {
        compose = true
        buildConfig = true // Enable BuildConfig generation
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
    }

    // ✅ NEW: Set consistent JVM target compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
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

    // Networking (Retrofit + OkHttp)
    implementation(libs.bundles.networking)
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.12")

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

    // Accompanist Pager for image preview
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

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

    // Firebase - Using BOM for version management
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")

    // Coil untuk memuat gambar profile
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")

    // Location Services for Geofencing
    implementation("com.google.android.gms:play-services-location:21.0.1")
}