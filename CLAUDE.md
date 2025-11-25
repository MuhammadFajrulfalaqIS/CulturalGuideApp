# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Cultural Navigation Android app built with modern Android architecture:
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Dependency Injection**: Hilt
- **Database**: Room
- **Maps**: Google Maps with Compose integration
- **Navigation**: Jetpack Navigation Compose

## Build Commands

```bash
# Build the app
./gradlew build

# Install debug APK
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Key Architecture Components

### 1. Map Screen Implementation
- **Location**: `app/src/main/java/com/example/cultural_navigation_papb/ui/screens/MapScreen.kt`
- **ViewModel**: `app/src/main/java/com/example/cultural_navigation_papb/data/viewmodels/MapsViewModel.kt`
- **Features**:
  - Real-time location tracking with permissions handling
  - Custom zoom controls
  - Search functionality for places
  - Google Maps integration with `@HiltViewModel`

### 2. Database Architecture
- **Database**: `AppDatabase.kt` - Room database with Place entity
- **DAO**: `PlaceDao.kt` - Data access object for place operations
- **Repository**: `PlaceRepository.kt` - Abstraction layer between ViewModel and database
- **DI Module**: `DatabaseModule.kt` - Hilt module for database dependencies (currently commented out)

### 3. Data Flow
```
UI (Compose) ↔ ViewModel ↔ Repository ↔ Database (Room)
                    ↓
               Location Services
```

### 4. Navigation Structure
- **NavHost**: `Navigation.kt` with Jetpack Navigation Compose
- **Routes**: Home → Map → List → Profile/Detail
- **Map Screen**: Integrates with detail screen via `onNavigateToDetail(placeId)`

## Key Dependencies (from libs.versions.toml)

- **Maps**: `maps-compose` (6.12.1), `play-services-maps` (18.2.0), `play-services-location` (21.0.1)
- **Room**: (2.8.3) with KSP compiler
- **Hilt**: (2.57.1) for dependency injection
- **Compose BOM**: (2025.10.01)
- **Networking**: Retrofit (3.0.0) for future API integration

## Map Screen Specifics

### Permission Handling
- Uses Accompanist permissions library for location access
- Automatically requests permission on component launch
- Enables/disables myLocation layer based on permission status

### Location Services
- FusedLocationProviderClient for real-time updates
- 5-second interval with 2-second minimum update interval
- High accuracy priority with configurable parameters
- Proper cleanup in ViewModel.onCleared()

### Map Features
- Custom zoom controls with animation (500ms duration)
- Search bar with place lookup functionality
- Camera position state management
- Default location: Prambanan Temple (-7.7520, 110.4891)

## Database Schema

### Place Entity
- Located in `app/src/main/java/com/example/cultural_navigation_papb/data/models/Place.kt`
- Supports cultural place metadata for the navigation app

### Database Configuration
- Database name: `cultural_navigation_database`
- Version: 1 with destructive migration fallback
- Singleton pattern with thread-safe initialization

## Development Notes

### Hilt Integration
- ViewModels use `@HiltViewModel` and `@Inject constructor`
- Database module is prepared but commented out - uncomment when activating database features
- Proper context injection with `@ApplicationContext`

### State Management
- Uses Kotlin StateFlow and MutableStateFlow for reactive UI
- CollectAsState() in Composables for state observation
- Proper lifecycle management with LaunchedEffect effects

### Location Updates
- Implements proper location callback with cleanup
- Handles permission states gracefully
- Provides fallback to default location when user location unavailable

## Testing
- Unit tests with JUnit
- Instrumentation tests with AndroidX Test
- Compose testing support included

## Future Enhancements
- Retrofit integration for place search APIs
- Full database activation (uncomment DatabaseModule)
- Offline map support
- Advanced place clustering
- Navigation routing features