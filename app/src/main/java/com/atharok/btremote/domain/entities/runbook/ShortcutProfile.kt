package com.atharok.btremote.domain.entities.runbook

import kotlinx.serialization.Serializable

@Serializable
data class ShortcutProfile(
    val id: String,
    val name: String,
    val snippets: List<RunbookSnippet> = emptyList()
)

object ShortcutHubDefaults {
    fun profiles(): List<ShortcutProfile> = listOf(
        ShortcutProfile(
            id = "starter",
            name = "Starter",
            snippets = RunbookSnippet.defaultSnippets()
        )
    )
}
