package eu.kanade.tachiyomi.data.backup.models

import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tachiyomi.domain.track.model.Track

@Suppress("DEPRECATION")
class BackupTrackingCompatibilityTest {

    @Test
    fun `backup conversion preserves an unknown tracker record`() {
        val backupTracking = backupTrackMapper(
            17,
            23,
            UNKNOWN_TRACKER_ID,
            4_294_967_296,
            9_876_543_210,
            "Manga no 42",
            12.5,
            321,
            7,
            8.75,
            "https://tracker.invalid/manga/4294967296",
            1_700_000_000_000,
            1_800_000_000_000,
            true,
        )

        assertEquals(
            BackupTracking(
                syncId = UNKNOWN_TRACKER_ID.toInt(),
                libraryId = 9_876_543_210,
                mediaIdInt = 0,
                mediaId = 4_294_967_296,
                trackingUrl = "https://tracker.invalid/manga/4294967296",
                title = "Manga no 42",
                lastChapterRead = 12.5F,
                totalChapters = 321,
                score = 8.75F,
                status = 7,
                startedReadingDate = 1_700_000_000_000,
                finishedReadingDate = 1_800_000_000_000,
                private = true,
            ),
            backupTracking,
        )
        assertEquals(
            Track(
                id = -1,
                mangaId = -1,
                trackerId = UNKNOWN_TRACKER_ID,
                remoteId = 4_294_967_296,
                libraryId = 9_876_543_210,
                title = "Manga no 42",
                lastChapterRead = 12.5,
                totalChapters = 321,
                status = 7,
                score = 8.75,
                remoteUrl = "https://tracker.invalid/manga/4294967296",
                startDate = 1_700_000_000_000,
                finishDate = 1_800_000_000_000,
                private = true,
            ),
            backupTracking.getTrackImpl(),
        )
    }

    @Test
    fun `legacy media id is restored when the current media id is absent`() {
        val backupTracking = BackupTracking(
            syncId = UNKNOWN_TRACKER_ID.toInt(),
            libraryId = 9,
            mediaIdInt = 123_456,
            mediaId = 0,
        )

        assertEquals(123_456L, backupTracking.getTrackImpl().remoteId)
    }

    @Test
    fun `manga protobuf round trip preserves an unknown tracker record`() {
        val tracking = BackupTracking(
            syncId = UNKNOWN_TRACKER_ID.toInt(),
            libraryId = 9_876_543_210,
            mediaId = 4_294_967_296,
            trackingUrl = "https://tracker.invalid/manga/4294967296",
            title = "Manga no 42",
            lastChapterRead = 12.5F,
            totalChapters = 321,
            score = 8.75F,
            status = 7,
            startedReadingDate = 1_700_000_000_000,
            finishedReadingDate = 1_800_000_000_000,
            private = true,
        )
        val manga = BackupManga(
            source = 123,
            url = "/manga/42",
            title = "Manga no 42",
            tracking = listOf(tracking),
        )

        val encoded = ProtoBuf.encodeToByteArray(BackupManga.serializer(), manga)
        val decoded = ProtoBuf.decodeFromByteArray(BackupManga.serializer(), encoded)

        assertEquals(123L, decoded.source)
        assertEquals("/manga/42", decoded.url)
        assertEquals(listOf(tracking), decoded.tracking)
    }

    @Test
    fun `legacy manga protobuf decodes its tag 18 tracker record`() {
        val decoded = ProtoBuf.decodeFromByteArray(
            BackupManga.serializer(),
            LEGACY_MANGA_WITH_TRACKING,
        )

        assertEquals(7L, decoded.source)
        assertEquals("legacy-url", decoded.url)
        assertEquals(
            listOf(
                BackupTracking(
                    syncId = UNKNOWN_TRACKER_ID.toInt(),
                    libraryId = 9,
                    mediaIdInt = 123_456,
                    title = "preserved",
                ),
            ),
            decoded.tracking,
        )
        assertEquals(123_456L, decoded.tracking.single().getTrackImpl().remoteId)
    }

    private companion object {
        const val UNKNOWN_TRACKER_ID = 4_242L

        /**
         * A minimal legacy protobuf payload with BackupManga tracking field 18.
         * Its nested record uses the legacy int media ID field 3.
         */
        val LEGACY_MANGA_WITH_TRACKING = """
            08 07
            12 0a 6c 65 67 61 63 79 2d 75 72 6c
            92 01 14
            08 92 21
            10 09
            18 c0 c4 07
            2a 09 70 72 65 73 65 72 76 65 64
        """.hexToByteArray()

        fun String.hexToByteArray(): ByteArray {
            return filterNot(Char::isWhitespace)
                .chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }
    }
}
