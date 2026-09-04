package eu.kanade.tachiyomi.data.backup.create

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BackupOptionsCompatibilityTest {

    @Test
    fun `tracking remains enabled in positional option slot 3 by default`() {
        assertTrue(BackupOptions().asBooleanArray()[TRACKING_INDEX])
    }

    @Test
    fun `legacy option array retains tracking and all later positions`() {
        val serialized = booleanArrayOf(
            false,
            false,
            true,
            false,
            true,
            false,
            true,
            true,
            false,
            true,
        )

        val options = BackupOptions.fromBooleanArray(serialized)

        assertEquals(
            BackupOptions(
                libraryEntries = false,
                categories = false,
                chapters = true,
                tracking = false,
                history = true,
                readEntries = false,
                appSettings = true,
                extensionStores = true,
                sourceSettings = false,
                privateSettings = true,
            ),
            options,
        )
        assertArrayEquals(serialized, options.asBooleanArray())
    }

    private companion object {
        const val TRACKING_INDEX = 3
    }
}
