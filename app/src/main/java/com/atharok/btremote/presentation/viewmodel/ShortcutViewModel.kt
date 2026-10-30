package com.atharok.btremote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atharok.btremote.domain.entities.runbook.ShortcutProfile
import com.atharok.btremote.domain.usecases.ShortcutUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShortcutViewModel(
    private val useCase: ShortcutUseCase
) : ViewModel() {

    val profilesFlow: Flow<List<ShortcutProfile>> = useCase.profilesFlow

    fun saveProfiles(profiles: List<ShortcutProfile>) = viewModelScope.launch {
        useCase.saveProfiles(profiles)
    }
}
