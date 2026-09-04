package tachiyomi.domain.track.interactor

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import tachiyomi.domain.track.model.Track
import tachiyomi.domain.track.repository.TrackRepository

class LegacyTrackInteractorsTest {

    private val repository = mockk<TrackRepository>()

    @Test
    fun `track read failures propagate to backup restore`() = runTest {
        val failure = IllegalStateException("read failed")
        coEvery { repository.getTracksByMangaId(42) } throws failure

        val thrown = captureFailure {
            GetTracks(repository).await(42)
        }

        assertSame(failure, thrown)
    }

    @Test
    fun `track write failures propagate to backup restore`() = runTest {
        val tracks = listOf(mockk<Track>())
        val failure = IllegalStateException("write failed")
        coEvery { repository.insertAll(tracks) } throws failure

        val thrown = captureFailure {
            InsertTrack(repository).awaitAll(tracks)
        }

        assertSame(failure, thrown)
    }

    private suspend fun captureFailure(block: suspend () -> Unit): Throwable? {
        return try {
            block()
            null
        } catch (error: Throwable) {
            error
        }
    }
}
