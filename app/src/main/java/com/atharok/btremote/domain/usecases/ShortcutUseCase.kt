package com.atharok.btremote.domain.usecases

import com.atharok.btremote.domain.entities.runbook.ShortcutProfile
import com.atharok.btremote.domain.repositories.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class ShortcutUseCase(
    private val dataStoreRepository: DataStoreRepository
) {

    val profilesFlow: Flow<List<ShortcutProfile>> get() = dataStoreRepository.shortcutProfilesFlow

    suspend fun saveProfiles(profiles: List<ShortcutProfile>) {
        dataStoreRepository.saveShortcutProfiles(profiles)
    }
}
