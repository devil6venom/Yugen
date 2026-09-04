package tachiyomi.domain.track.interactor

import dev.zacsweers.metro.Inject
import tachiyomi.domain.track.model.Track
import tachiyomi.domain.track.repository.TrackRepository

@Inject
class InsertTrack(
    private val trackRepository: TrackRepository,
) {

    suspend fun awaitAll(tracks: List<Track>) = trackRepository.insertAll(tracks)
}
