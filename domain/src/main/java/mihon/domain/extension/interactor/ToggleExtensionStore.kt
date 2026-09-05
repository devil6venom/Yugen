package mihon.domain.extension.interactor

import dev.zacsweers.metro.Inject
import mihon.domain.extension.repository.ExtensionStoreRepository

@Inject
class ToggleExtensionStore(
    private val repository: ExtensionStoreRepository,
) {
    suspend operator fun invoke(indexUrl: String, enabled: Boolean) {
        repository.updateEnabled(indexUrl, enabled)
    }
}
