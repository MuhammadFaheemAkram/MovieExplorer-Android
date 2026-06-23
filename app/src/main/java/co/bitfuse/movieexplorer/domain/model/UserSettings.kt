package co.bitfuse.movieexplorer.domain.model

data class UserSettings(
    val darkModeEnabled: Boolean = false,
    val contentLanguage: ContentLanguage = ContentLanguage.English,
)
