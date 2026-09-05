package mihon.data.extension.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import eu.kanade.tachiyomi.extension.model.Extension
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.supervisorScope
import logcat.LogPriority
import mihon.data.extension.service.ExtensionStoreService
import mihon.domain.extension.model.ExtensionStore
import mihon.domain.extension.repository.ExtensionStoreRepository
import tachiyomi.core.common.util.system.logcat
import tachiyomi.data.Database
import tachiyomi.data.subscribeToList
import tachiyomi.data.subscribeToOne

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ExtensionStoreRepositoryImpl(
    private val service: ExtensionStoreService,
    private val database: Database,
) : ExtensionStoreRepository {
    override suspend fun insert(indexUrl: String): Result<Unit> {
        return service.fetch(indexUrl).mapCatching { upsert(it) }
    }

    override suspend fun insertFromPreference(indexUrl: String, name: String) {
        database.extension_storeQueries.upsert(
            indexUrl = indexUrl,
            name = name,
            badgeLabel = name,
            signingKey = "NO_SIGNING_KEY",
            contactWebsite = indexUrl,
            isLegacy = false,
            extensionListUrl = null,
            enabled = true,
        )
    }

    override suspend fun refreshAll() {
        try {
            database.extension_storeQueries.getAll().awaitAsList().forEach { store ->
                service.fetch(store.index_url)
                    .onSuccess { fetchedStore ->
                        database.transaction {
                            upsert(fetchedStore.copy(enabled = store.enabled))
                            if (store.index_url != fetchedStore.indexUrl) {
                                database.extension_storeQueries.delete(store.index_url)
                            }
                        }
                    }
                    .onFailure {
                        logcat(LogPriority.ERROR, it) {
                            "Failed to refresh extension store '${store.name} (${store.index_url})'"
                        }
                    }
            }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }

    private suspend fun upsert(store: ExtensionStore) {
        database.extension_storeQueries.upsert(
            indexUrl = store.indexUrl,
            name = store.name,
            badgeLabel = store.badgeLabel,
            signingKey = store.signingKey,
            contactWebsite = store.contact.website,
            isLegacy = store.isLegacy,
            extensionListUrl = store.extensionListUrl,
            enabled = store.enabled,
        )
    }

    override suspend fun fetchExtensions(): List<Extension.Available> {
        return try {
            supervisorScope {
                database.extension_storeQueries.getAll(::extensionStoreMapper)
                    .awaitAsList()
                    .filter { it.enabled }
                    .map { store ->
                        async {
                            service.getExtensions(store).onFailure {
                                this@ExtensionStoreRepositoryImpl.logcat(LogPriority.ERROR, it) {
                                    "Failed to fetch extensions for store '${store.name} (${store.indexUrl})'"
                                }
                            }
                        }
                    }
                    .awaitAll()
                    .flatMap { it.getOrDefault(emptyList()) }
            }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }

    override suspend fun getAll(): List<ExtensionStore> {
        return database.extension_storeQueries.getAll(::extensionStoreMapper).awaitAsList()
    }

    override fun getAllAsFlow(): Flow<List<ExtensionStore>> {
        return database.extension_storeQueries.getAll(::extensionStoreMapper).subscribeToList()
    }

    override fun getCountAsFlow(): Flow<Long> {
        return database.extension_storeQueries
            .getCount()
            .subscribeToOne()
    }

    override suspend fun updateEnabled(indexUrl: String, enabled: Boolean) {
        database.extension_storeQueries.updateEnabled(enabled, indexUrl)
    }

    override suspend fun remove(indexUrl: String) {
        database.extension_storeQueries.delete(indexUrl)
    }

    private fun extensionStoreMapper(
        indexUrl: String,
        name: String,
        badgeLabel: String,
        signingKey: String,
        contactWebsite: String,
        isLegacy: Boolean,
        extensionListUrl: String?,
        enabled: Boolean,
    ): ExtensionStore = ExtensionStore(
        indexUrl = indexUrl,
        name = name,
        badgeLabel = badgeLabel,
        signingKey = signingKey,
        contact = ExtensionStore.Contact(
            website = contactWebsite,
        ),
        isLegacy = isLegacy,
        extensionListUrl = extensionListUrl,
        enabled = enabled,
    )
}
