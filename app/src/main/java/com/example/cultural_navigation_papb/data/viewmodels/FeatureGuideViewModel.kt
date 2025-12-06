package com.example.cultural_navigation_papb.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cultural_navigation_papb.data.datastore.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeatureGuideViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    val isFeatureGuideCompleted: StateFlow<Boolean> = onboardingPreferences.isFeatureGuideCompleted
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setFeatureGuideCompleted() {
        viewModelScope.launch {
            onboardingPreferences.setFeatureGuideCompleted(true)
        }
    }

    fun resetFeatureGuide() {
        viewModelScope.launch {
            onboardingPreferences.resetFeatureGuide()
        }
    }
}

