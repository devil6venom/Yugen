package tachiyomi.domain.library.model

sealed interface LibraryGroup {

    data object BY_DEFAULT : LibraryGroup
    data object BY_SOURCE : LibraryGroup
    data object BY_STATUS : LibraryGroup
    data object BY_TRACKING_STATUS : LibraryGroup
    data object BY_LANGUAGE : LibraryGroup

    object Serializer {
        fun deserialize(serialized: String): LibraryGroup {
            return LibraryGroup.deserialize(serialized)
        }

        fun serialize(value: LibraryGroup): String {
            return value.serialize()
        }
    }

    companion object {
        val values by lazy { setOf(BY_DEFAULT, BY_SOURCE, BY_STATUS, BY_TRACKING_STATUS, BY_LANGUAGE) }
        val default = BY_DEFAULT

        fun deserialize(serialized: String): LibraryGroup {
            return when (serialized) {
                "BY_DEFAULT" -> BY_DEFAULT
                "BY_SOURCE" -> BY_SOURCE
                "BY_STATUS" -> BY_STATUS
                "BY_TRACKING_STATUS" -> BY_TRACKING_STATUS
                "BY_LANGUAGE" -> BY_LANGUAGE
                else -> default
            }
        }
    }

    fun serialize(): String {
        return when (this) {
            BY_DEFAULT -> "BY_DEFAULT"
            BY_SOURCE -> "BY_SOURCE"
            BY_STATUS -> "BY_STATUS"
            BY_TRACKING_STATUS -> "BY_TRACKING_STATUS"
            BY_LANGUAGE -> "BY_LANGUAGE"
        }
    }
}
