package tachiyomi.domain.track.repository

import tachiyomi.domain.track.model.Track

interface TrackRepository {

    suspend fun getTracksByMangaId(mangaId: Long): List<Track>

    suspend fun insertAll(tracks: List<Track>)
}
