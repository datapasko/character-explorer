package com.tapasco.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapasco.characters.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val isDarkTheme: StateFlow<Boolean?> = preferencesRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = null,
        )

    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkTheme(isDarkTheme)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
