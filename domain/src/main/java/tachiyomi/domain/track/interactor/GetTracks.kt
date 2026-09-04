package tachiyomi.domain.track.interactor

import dev.zacsweers.metro.Inject
import tachiyomi.domain.track.model.Track
import tachiyomi.domain.track.repository.TrackRepository

@Inject
class GetTracks(
    private val trackRepository: TrackRepository,
) {

    suspend fun await(mangaId: Long): List<Track> = trackRepository.getTracksByMangaId(mangaId)
}
