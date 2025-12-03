# 📚 Dependencies & Libraries Documentation

Dokumentasi lengkap semua library dan dependency yang digunakan di CulturalGuideApp.

## 🎯 Core Dependencies

### Android & Kotlin
```gradle
- Kotlin Version: 2.0.21
- Compose Compiler: 1.7.3
- Android Gradle Plugin: 8.7.3
- Min SDK: 24 (Android 7.0)
- Target SDK: 36
- Compile SDK: 36
```

**Fungsi**: Foundation framework untuk development Android dengan Kotlin dan Jetpack Compose.

---

## 🎨 UI & Compose

### Jetpack Compose
```gradle
androidx.compose.ui:ui
androidx.compose.ui:ui-graphics
androidx.compose.ui:ui-tooling-preview
androidx.compose.material3:material3
androidx.compose.foundation:foundation
androidx.activity:activity-compose
```

**Fungsi**: 
- Membuat UI modern dengan declarative approach
- Material Design 3 untuk consistent design language
- Preview tools untuk fast development

### Material Icons Extended
```gradle
androidx.compose.material:material-icons-extended
```

**Fungsi**: Icon library lengkap untuk UI (rating stars, navigation, etc.)

---

## 🗺️ Maps & Location

### Google Maps Compose
```gradle
com.google.maps.android:maps-compose:4.3.3
com.google.android.gms:play-services-maps:18.2.0
com.google.android.gms:play-services-location:21.0.1
```

**Fungsi**:
- Tampilkan peta interaktif di app
- Show tempat wisata dengan markers
- Real-time location tracking
- Geofencing untuk notifikasi berbasis lokasi

---

## 🏛️ Architecture Components

### Room Database
```gradle
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1
```

**Fungsi**:
- Local database untuk offline storage
- Cache place data
- Store user favorites & reviews locally
- Sync dengan Firestore untuk cloud backup

### Lifecycle & ViewModel
```gradle
androidx.lifecycle:lifecycle-runtime-ktx
androidx.lifecycle:lifecycle-viewmodel-compose
```

**Fungsi**:
- Manage app lifecycle
- State management dengan ViewModel
- Handle configuration changes

---

## 💉 Dependency Injection

### Hilt (Dagger)
```gradle
com.google.dagger:hilt-android:2.57.1
com.google.dagger:hilt-compiler:2.57.1
androidx.hilt:hilt-navigation-compose:1.2.0
androidx.hilt:hilt-work:1.1.0
```

**Fungsi**:
- Dependency injection framework
- Manage objects lifecycle automatically
- Easier testing & modular code
- Integration dengan Navigation & WorkManager

---

## 🔥 Firebase & Cloud

### Firebase Services
```gradle
com.google.firebase:firebase-bom:33.7.0
com.google.firebase:firebase-analytics-ktx
com.google.firebase:firebase-auth-ktx
com.google.firebase:firebase-firestore-ktx
com.google.firebase:firebase-messaging-ktx
com.google.firebase:firebase-functions-ktx
```

**Fungsi**:
- **Firestore**: Cloud database untuk reviews & user data
- **Auth**: User authentication (email/password, Google sign-in)
- **Analytics**: Track user behavior & app usage
- **Messaging**: Push notifications untuk promo/update
- **Functions**: Serverless backend logic

**Review System Implementation**:
```
User submits review → ReviewViewModel → PlaceRepository → FirestoreService
                                                         ↓
                                                   Cloud Storage
                                                         ↓
                                                   Fallback → Room DB
```

---

## 🌐 Networking

### Retrofit & OkHttp
```gradle
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.squareup.okhttp3:okhttp:5.0.0-alpha.12
com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.12
```

**Fungsi**:
- HTTP client untuk API calls
- Google Directions API untuk navigation
- Logging untuk debugging network requests
- JSON parsing dengan GSON

---

## 🖼️ Image Loading

### Coil
```gradle
io.coil-kt:coil-compose:2.5.0
```

**Fungsi**:
- Asynchronous image loading
- Load dari URL, file, atau resource
- Automatic caching & memory management
- Compose-native integration

**Use Cases**:
- Place images
- User profile pictures
- Review photos (up to 5 per review)

---

## 📱 Navigation

### Jetpack Navigation Compose
```gradle
androidx.navigation:navigation-compose:2.9.5
```

**Fungsi**:
- Screen navigation dengan type-safety
- Deep linking support
- Back stack management
- Arguments passing between screens

**Screens**:
- OnBoarding → SignIn → Home → DetailScreen → ReviewDialog
- MapScreen, ListScreen, ProfileScreen, InboxScreen

---

## ⚙️ Background Processing

### WorkManager
```gradle
androidx.work:work-runtime-ktx:2.9.0
androidx.hilt:hilt-work:1.1.0
```

**Fungsi**:
- Background tasks scheduling
- Sync local reviews to Firestore when online
- Periodic data refresh
- Guaranteed execution (bahkan setelah app closed)

---

## 🔐 Permissions

### Accompanist Permissions
```gradle
com.google.accompanist:accompanist-permissions:0.34.0
```

**Fungsi**:
- Handle runtime permissions dengan Compose
- Location permission untuk maps
- Camera/storage permission untuk review photos

---

## 🧪 Testing

### Test Dependencies
```gradle
junit:junit:4.13.2
androidx.test:junit:1.1.5
androidx.test:espresso-core:3.5.1
androidx.compose.ui:ui-test-junit4
```

**Fungsi**:
- Unit testing
- UI testing dengan Compose
- Integration testing

---

## 🛠️ Build Tools

### KSP (Kotlin Symbol Processing)
```gradle
com.google.devtools.ksp:2.2.21-2.0.4
```

**Fungsi**:
- Annotation processing untuk Room, Hilt
- Faster compilation dibanding KAPT
- Better incremental builds

### Google Services
```gradle
com.google.gms:google-services:4.4.4
```

**Fungsi**:
- Integration dengan Firebase services
- Auto-configure Firebase SDK
- Manage google-services.json

---

## 📊 Data Flow Architecture

```
UI Layer (Composables)
    ↓
ViewModel Layer (State Management)
    ↓
Repository Layer (Data Source Coordination)
    ↓
    ├─→ Remote: Firestore (Cloud)
    ├─→ Local: Room Database (Offline)
    └─→ API: Retrofit (Directions, etc)
```

### Review System Flow
```
ImprovedReviewDialog (UI)
    ↓
ReviewViewModel (State)
    ↓
PlaceRepository (Coordination)
    ↓
    ├─→ FirestoreService → Cloud Storage ✓
    └─→ ReviewDao → Local Database ✓
```

---

## 🎨 Theme & Design

### Color Scheme
```kotlin
EarthBrown   = Color(0xFF3E2723)  // Primary
LightBrown   = Color(0xFF6D4C41)  // Secondary
OrangeAccent = Color(0xFFFF6F00)  // Accent (stars, CTAs)
WarmWhite    = Color(0xFFFFFEF7)  // Background
```

### Typography
- **Headers**: 20-22sp, Bold
- **Body**: 14-15sp, Regular
- **Captions**: 11-12sp, Light

---

## ⚡ Performance Optimizations

### Gradle Configuration
```properties
org.gradle.jvmargs=-Xmx4096m        # Increase heap size
org.gradle.parallel=true             # Parallel builds
org.gradle.caching=true              # Build caching
kotlin.incremental=true              # Incremental compilation
android.enableR8.fullMode=true       # Full R8 optimization
```

### Best Practices
- ✅ LazyColumn untuk list (efficient scrolling)
- ✅ remember untuk avoid recomposition
- ✅ Coil untuk automatic image caching
- ✅ Room untuk offline-first approach
- ✅ Hilt untuk singleton management

---

## 🚀 Getting Started

### Prerequisites
```bash
- Android Studio Ladybug | 2024.2.1
- JDK 21 (bundled with Android Studio)
- Android SDK 36
- Gradle 8.13
- Minimum 4GB RAM (8GB recommended)
```

### Setup Steps
1. Clone repository
2. Add `google-services.json` ke folder `app/`
3. Create `local.properties`:
   ```properties
   GOOGLE_MAPS_API_KEY=your_key_here
   GOOGLE_DIRECTIONS_API_KEY=your_key_here
   CLOUDINARY_CLOUD_NAME=your_cloudname
   CLOUDINARY_UPLOAD_PRESET=your_preset
   ```
4. Enable Firestore di Firebase Console
5. Sync Gradle & Build

---

## 📝 Version History

### Current Version: 1.0.0
- ✅ Firebase Firestore integration untuk reviews
- ✅ Photo upload support (up to 5 photos)
- ✅ Offline-first dengan Room database
- ✅ Real-time location tracking
- ✅ Geofencing notifications
- ✅ Material Design 3 UI

### Roadmap
- 🔜 Firebase Storage untuk photo uploads
- 🔜 Image compression before upload
- 🔜 Review moderation system
- 🔜 Social sharing features
- 🔜 Multi-language support

---

## 🐛 Known Issues & Fixes

### Issue: Gradle Daemon Crash
**Error**: "The daemon has disappeared"
**Fix**: 
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8 -XX:MaxMetaspaceSize=1024m
```

### Issue: "Unable to strip libraries"
**Warning**: `libandroidx.graphics.path.so`
**Status**: Non-critical warning, tidak affect functionality

### Issue: Reviews tidak muncul
**Fix**: Already fixed - proper Firestore integration dengan fallback ke Room

---

## 📞 Support

Jika ada issue:
1. Check logcat: `adb logcat | grep -E "ReviewViewModel|FirestoreService|PlaceRepository"`
2. Verify Firebase config: `google-services.json` exists
3. Check network: Firestore requires internet connection
4. Clear cache: `./gradlew clean`
5. Rebuild: `./gradlew build`

---

## 📄 License

This project uses various open-source libraries. Check individual library licenses:
- Apache 2.0: Android libraries, Kotlin, Compose, Hilt, Retrofit, OkHttp, Room
- MIT: Coil
- Firebase: Google Terms of Service

---

**Last Updated**: December 2, 2025
**Maintainer**: Cultural Navigation Team
**Version**: 1.0.0

