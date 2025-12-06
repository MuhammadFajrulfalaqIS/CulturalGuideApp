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

---