package eu.kanade.domain.track.service

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DelayedTrackingUpdateJobTest {

    @Test
    fun `queue cleanup reports a successful synchronous commit`() {
        val preferences = mockPreferences(commitResult = true)

        assertTrue(clearLegacyTrackingQueue(preferences.first))

        verifySequence {
            preferences.first.edit()
            preferences.second.clear()
            preferences.second.commit()
        }
    }

    @Test
    fun `queue cleanup reports a failed synchronous commit`() {
        val preferences = mockPreferences(commitResult = false)

        assertFalse(clearLegacyTrackingQueue(preferences.first))

        verifySequence {
            preferences.first.edit()
            preferences.second.clear()
            preferences.second.commit()
        }
    }

    private fun mockPreferences(commitResult: Boolean): Pair<SharedPreferences, SharedPreferences.Editor> {
        val preferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { preferences.edit() } returns editor
        every { editor.clear() } returns editor
        every { editor.commit() } returns commitResult
        return preferences to editor
    }
}
