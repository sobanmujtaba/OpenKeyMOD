package com.atharok.btremote

import com.atharok.btremote.domain.entities.runbook.ShortcutHubDefaults
import com.atharok.btremote.domain.entities.runbook.ShortcutProfile
import com.atharok.btremote.domain.entities.runbook.ShortcutProfileCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShortcutProfileCodecTest {

    @Test
    fun defaultProfiles_roundTripThroughJson() {
        val profiles = ShortcutHubDefaults.profiles()
        val decoded = ShortcutProfileCodec.decode(ShortcutProfileCodec.encode(profiles))

        assertEquals(profiles, decoded)
    }

    @Test
    fun invalidJson_returnsNull() {
        assertNull(ShortcutProfileCodec.decode("not json"))
        assertNull(ShortcutProfileCodec.decode(""))
    }

    @Test
    fun blankIds_areGeneratedAndDuplicatesAreResolved() {
        val decoded = ShortcutProfileCodec.decode(
            """
            [
              {
                "id": "",
                "name": "",
                "snippets": [
                  {"id": "same", "title": "", "command": "a", "category": ""},
                  {"id": "same", "title": "B", "command": "b", "category": "Cat", "delayMs": 999}
                ]
              },
              {"id": "", "name": "Second", "snippets": []}
            ]
            """.trimIndent()
        )

        assertNotNull(decoded)
        val profiles = decoded.orEmpty()
        assertEquals(2, profiles.size)
        assertTrue(profiles[0].id.isNotBlank())
        assertEquals("Untitled profile", profiles[0].name)
        assertEquals(2, profiles[0].snippets.size)
        assertTrue(profiles[0].snippets[0].id != profiles[0].snippets[1].id)
        assertEquals("Untitled snippet", profiles[0].snippets[0].title)
        assertEquals("General", profiles[0].snippets[0].category)
        assertEquals(150L, profiles[0].snippets[1].delayMs)
        assertTrue(profiles[1].id.isNotBlank())
    }

    @Test
    fun emptyProfileList_roundTrips() {
        assertEquals(emptyList<ShortcutProfile>(), ShortcutProfileCodec.decode("[]"))
    }
}
