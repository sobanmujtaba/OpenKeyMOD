package com.atharok.btremote.domain.entities.runbook

import kotlinx.serialization.json.Json
import java.util.UUID

object ShortcutProfileCodec {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encode(profiles: List<ShortcutProfile>): String = json.encodeToString(profiles)

    fun decode(raw: String): List<ShortcutProfile>? {
        if (raw.isBlank()) return null
        return runCatching {
            json.decodeFromString<List<ShortcutProfile>>(raw).normalized()
        }.getOrNull()
    }

    private fun List<ShortcutProfile>.normalized(): List<ShortcutProfile> {
        val profileIds = mutableSetOf<String>()
        return map { profile ->
            val profileId = uniqueId(profile.id, profileIds)
            val snippetIds = mutableSetOf<String>()
            profile.copy(
                id = profileId,
                name = profile.name.ifBlank { "Untitled profile" },
                snippets = profile.snippets.map { snippet ->
                    snippet.copy(
                        id = uniqueId(snippet.id, snippetIds),
                        title = snippet.title.ifBlank { "Untitled snippet" },
                        command = snippet.command,
                        category = snippet.category.ifBlank { "General" },
                        delayMs = snippet.delayMs.coerceIn(5L, 150L)
                    )
                }
            )
        }
    }

    private fun uniqueId(id: String, used: MutableSet<String>): String {
        var candidate = id.ifBlank { UUID.randomUUID().toString() }
        while (!used.add(candidate)) {
            candidate = UUID.randomUUID().toString()
        }
        return candidate
    }
}
