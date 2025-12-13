# Cultural Guide App - Prambanan Navigation

**Team Members:**
- Izzul
- Iqbal
- Jhon

---

## 📱 Fitur Aplikasi

### 1. **Map Screen dengan Interactive Features**
- **Draggable Bottom Sheet**
  - State: `ModalBottomSheetState` (Hidden, PartiallyExpanded, Expanded)
  - Filter lokasi: ALL, VISITED, UNVISITED
  - Menampilkan 3 item saat half-expanded, semua item saat fully expanded
  - Auto-show ketika ada lokasi nearby

- **Pop-up Destination Detail**
  - Photo carousel dengan 4 gambar
  - Auto-scroll setiap 3 detik
  - Indicator dots & page counter (e.g., "1 / 4")
  - Informasi lengkap tempat wisata
  - Tombol navigate untuk route planning

- **Markup Location & Geofencing**
  - Radius deteksi: **30 meter**
  - Auto-register geofence untuk lokasi yang belum dikunjungi
  - Custom marker icon untuk visited/unvisited places
  - Real-time location tracking

- **Route with Directions API**
  - Menggunakan **Google Directions API**
  - Mode: Walking directions
  - Support alternative routes
  - Custom waypoints untuk area kompleks Prambanan
  - Polyline rendering di map dengan smooth path
  - Distance & duration estimation

### 2. **Detail Screen**
- **Photo Review dengan Upload Gambar**
  - Multi-photo carousel (swipeable)
  - Upload foto maksimal 5 gambar
  - Camera capture & gallery picker
  - Image preview sebelum submit
  - Compress & optimize foto otomatis
  - Real-time photo display di review section

- **Review System**
  - Rating 1-5 bintang dengan skala 0.1
  - Text review dengan validasi
  - Photo attachments
  - Rating distribution chart
  - Helpful button untuk review
  - Real-time Firestore sync

- **🎙️ Speech Assistant (Audio Guide)**
  - **AI-Generated Narration**
    - Gemini AI generates engaging tour guide scripts
    - Smart narration in Bahasa Indonesia
    - Context-aware storytelling based on temple history
    - Cached narration untuk offline playback
  
  - **Hybrid Text-to-Speech System**
    - **Primary**: Android native TTS dengan Indonesian voice selection
    - **Fallback**: Cloud TTS menggunakan Google Translate API (gratis)
    - Auto-detect & switch jika voice Indonesia tidak tersedia
    - Support online & offline mode
  
  - **Audio Player Controls**
    - Play, Pause, Stop, Replay
    - Real-time progress bar
    - Playback speed control (0.5x - 2x)
    - Pitch adjustment untuk kualitas suara
  
  - **Voice Quality Options**
    - 4 Preset gaya tour guide:
      - **Professional**: Standard tour guide (speed 0.9x, pitch 1.0x)
      - **Friendly**: Ramah & hangat (speed 0.85x, pitch 1.15x) ⭐ Recommended
      - **Energetic**: Antusias & bersemangat (speed 1.0x, pitch 1.2x)
      - **Calm**: Tenang & meditatif (speed 0.75x, pitch 0.9x)
    - Prioritas voice: Google Indonesian Female/Male (network/local)
  
  - **Smart Features**
    - Location-based auto-trigger (radius 100m)
    - Background audio playback
    - Audio focus management (auto-pause saat phone call)
    - Continue from last position
    - Multi-language support (ID/EN)
    - Chunk-based streaming untuk narasi panjang

### 3. **Geofence & Notification System**
- **Location Detection**
  - Check nearby destination dalam radius 30m
  - Auto-detect ketika user masuk geofence area
  - Markup location berubah otomatis (unvisited → visited)
  - Update visit count di Firestore

- **Firebase Cloud Messaging (FCM)**
  - Push notification saat masuk area geofence
  - Notification payload berisi place info
  - Click notification → route ke review form
  - Auto-open review dialog di DetailScreen
  - Deep linking support

### 4. **Additional Features**
- **Offline Mode**
  - Download destinasi untuk akses offline
  - Local Room Database caching
  - FAB button untuk save/remove offline content

- **Authentication**
  - Firebase Authentication
  - Google Sign-In
  - User profile management

- **User Preferences**
  - Onboarding screen dengan DataStore
  - Profile customization
  - Visit history tracking

---

## 🛠️ Tech Stack

- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt/Dagger
- **Database:** Room (Local) + Firestore (Remote)
- **Maps:** Google Maps SDK + Directions API
- **Notifications:** Firebase Cloud Messaging (FCM)
- **Location:** Geofencing API, FusedLocationProvider
- **Image:** Coil, CameraX
- **AI & Voice:** Gemini AI, Android TTS, Cloud TTS (Google Translate API)
- **Audio:** MediaPlayer, AudioManager, TextToSpeech API
- **State Management:** Kotlin Flow, StateFlow
- **UI:** Jetpack Compose, Material 3

---

## 🔑 Key Components

- **GeofenceManager**: 30m radius detection & registration
- **DirectionsApiService**: Google Directions API integration
- **FCMNotificationManager**: Push notification handler
- **DraggableBottomSheet**: 3-state modal sheet (Hidden/Partial/Expanded)
- **PlaceDetailPopup**: Photo carousel dengan auto-scroll
- **ImprovedReviewDialog**: Multi-photo upload review form
- **🆕 AudioGuidePlayer**: Hybrid TTS system (Native + Cloud fallback)
- **🆕 NarrationGenerator**: Gemini AI-powered tour guide narration
- **🆕 CloudTTSProvider**: Google Translate TTS API untuk fallback
- **🆕 LocationService**: Proximity detection untuk auto-trigger audio guide

---

## 🎯 Speech Assistant Architecture

### Flow Diagram
```
User arrives at location (100m radius)
        ↓
Check location proximity
        ↓
[Option 1] Manual: User taps Audio Guide button
[Option 2] Auto: Geofence trigger notification
        ↓
Generate narration (Gemini AI)
        ↓
Cache to Room Database
        ↓
Text-to-Speech Conversion
    ├─ Try Native TTS (Indonesian voice)
    └─ Fallback to Cloud TTS (if not available)
        ↓
Audio Playback with Controls
        ↓
Progress tracking & Speed control
```

### Voice Selection Priority
1. **id-id-x-idd-network** - Google Indonesian Female (best quality)
2. **id-id-x-idm-network** - Google Indonesian Male
3. **id-id-x-idd-local** - Local Indonesian Female
4. **id-id-x-idm-local** - Local Indonesian Male
5. **Cloud TTS Fallback** - Google Translate API (internet required)

---

## 📝 Notes

- **Audio Guide**: Otomatis switch ke Cloud TTS jika HP tidak punya voice Indonesia
- **Gratis**: Menggunakan Google Translate TTS API (tanpa API key)
- **Offline Ready**: Narration di-cache, tapi Cloud TTS butuh internet
- **Rekomendasi**: Install "Google Text-to-Speech" dari Play Store untuk kualitas terbaik

---