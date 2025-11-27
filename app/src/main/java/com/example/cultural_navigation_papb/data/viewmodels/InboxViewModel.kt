package com.example.cultural_navigation_papb.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cultural_navigation_papb.data.database.AppDatabase
import com.example.cultural_navigation_papb.data.models.SavedPlace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboxViewModel(application: Application) : AndroidViewModel(application) {
    private val savedPlaceDao = AppDatabase.getDatabase(application).savedPlaceDao()

    // List barang di inbox
    private val _savedPlaces = MutableStateFlow<List<SavedPlace>>(emptyList())
    val savedPlaces = _savedPlaces.asStateFlow()

    init {
        fetchSavedPlaces()
    }

    private fun fetchSavedPlaces() {
        viewModelScope.launch {
            savedPlaceDao.getAllSavedPlaces().collect {
                _savedPlaces.value = it
            }
        }
    }

    // Fungsi Download (Simpan ke DB Lokal)
    fun downloadPlace(id: String, name: String, desc: String, imageResId: Int) {
        viewModelScope.launch {
            val place = SavedPlace(id, name, desc, imageResId)
            savedPlaceDao.insertSavedPlace(place)
        }
    }

    // Fungsi Hapus dari Inbox
    fun removePlace(id: String) {
        viewModelScope.launch {
            savedPlaceDao.deleteSavedPlace(id)
        }
    }

    // Cek apakah sudah didownload
    fun isDownloaded(id: String) = savedPlaceDao.isPlaceSaved(id)
}