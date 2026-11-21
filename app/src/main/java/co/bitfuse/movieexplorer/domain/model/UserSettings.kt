package co.bitfuse.movieexplorer.domain.model

data class UserSettings(
    val darkModeEnabled: Boolean = false,
    val contentLanguage: ContentLanguage = ContentLanguage.English,
    // Shows the add / edit / delete movie actions. This only hides UI on this device;
    // the backend does not check it (see docs/PHASE_7_SUMMARY.md).
    val movieManagementEnabled: Boolean = false,
)
