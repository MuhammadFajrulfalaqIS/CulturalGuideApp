package com.example.cultural_navigation_papb.ui.components.spotlight

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import com.example.cultural_navigation_papb.data.datastore.OnboardingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FeatureShowcaseManager(
    private val onboardingPreferences: OnboardingPreferences
) {
    private val _currentStep = mutableStateOf(0)
    val currentStep: State<Int> = _currentStep

    private val _isShowcaseActive = mutableStateOf(false)
    val isShowcaseActive: State<Boolean> = _isShowcaseActive

    private val _showcaseTargets = mutableStateListOf<SpotlightTarget>()
    val showcaseTargets: List<SpotlightTarget> = _showcaseTargets

    val currentTarget: SpotlightTarget?
        get() = if (_currentStep.value < _showcaseTargets.size) {
            _showcaseTargets[_currentStep.value]
        } else null

    fun startShowcase(targets: List<SpotlightTarget>) {
        _showcaseTargets.clear()
        _showcaseTargets.addAll(targets)
        _currentStep.value = 0
        _isShowcaseActive.value = true
    }

    fun nextStep() {
        if (_currentStep.value < _showcaseTargets.size - 1) {
            _currentStep.value++
        } else {
            completeShowcase()
        }
    }

    fun skipShowcase() {
        completeShowcase()
    }

    private fun completeShowcase() {
        _isShowcaseActive.value = false
        _currentStep.value = 0
        _showcaseTargets.clear()
    }

    suspend fun checkAndStartShowcase(targets: List<SpotlightTarget>) {
        val isCompleted = onboardingPreferences.isFeatureGuideCompleted.first()
        if (!isCompleted) {
            startShowcase(targets)
        }
    }

    suspend fun markShowcaseCompleted() {
        onboardingPreferences.setFeatureGuideCompleted(true)
        completeShowcase()
    }
}

@Composable
fun rememberFeatureShowcaseManager(
    onboardingPreferences: OnboardingPreferences
): FeatureShowcaseManager {
    return remember { FeatureShowcaseManager(onboardingPreferences) }
}

