package eu.kanade.tachiyomi.data.backup

import android.net.Uri
import dev.zacsweers.metro.Inject
import tachiyomi.domain.source.service.SourceManager

@Inject
class BackupFileValidator(
    private val sourceManager: SourceManager,
    private val backupDecoder: BackupDecoder,
) {

    /**
     * Checks for critical backup file data.
     *
     * @return List of missing sources.
     */
    suspend fun validate(uri: Uri): Results {
        val backup = try {
            backupDecoder.decode(uri)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

        val sources = backup.backupSources.associate { it.sourceId to it.name }
        val missingSources = sources
            .filterKeys { sourceManager.get(it) == null }
            .values.map {
                val id = it.toLongOrNull()
                if (id == null) {
                    it
                } else {
                    sourceManager.getOrStub(id).toString()
                }
            }
            .distinct()
            .sorted()

        return Results(missingSources)
    }

    data class Results(
        val missingSources: List<String>,
    )
}
