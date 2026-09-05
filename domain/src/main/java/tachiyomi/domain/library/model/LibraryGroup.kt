package tachiyomi.domain.library.model

sealed interface LibraryGroup {

    data object ByDefault : LibraryGroup
    data object BySource : LibraryGroup
    data object ByStatus : LibraryGroup
    data object ByTrackingStatus : LibraryGroup
    data object ByLanguage : LibraryGroup

    object Serializer {
        fun deserialize(serialized: String): LibraryGroup {
            return LibraryGroup.deserialize(serialized)
        }

        fun serialize(value: LibraryGroup): String {
            return value.serialize()
        }
    }

    companion object {
        val values by lazy { setOf(ByDefault, BySource, ByStatus, ByTrackingStatus, ByLanguage) }
        val default = ByDefault

        fun deserialize(serialized: String): LibraryGroup {
            return when (serialized) {
                "BY_DEFAULT" -> ByDefault
                "BY_SOURCE" -> BySource
                "BY_STATUS" -> ByStatus
                "BY_TRACKING_STATUS" -> ByTrackingStatus
                "BY_LANGUAGE" -> ByLanguage
                else -> default
            }
        }
    }

    fun serialize(): String {
        return when (this) {
            ByDefault -> "BY_DEFAULT"
            BySource -> "BY_SOURCE"
            ByStatus -> "BY_STATUS"
            ByTrackingStatus -> "BY_TRACKING_STATUS"
            ByLanguage -> "BY_LANGUAGE"
        }
    }
}
