package co.bitfuse.movieexplorer.domain.model

enum class ContentLanguage(
    val code: String,
    val label: String,
) {
    English(code = "en", label = "English"),
    Spanish(code = "es", label = "Spanish"),
    Urdu(code = "ur", label = "Urdu");

    companion object {
        fun fromCode(code: String): ContentLanguage =
            entries.firstOrNull { language -> language.code == code } ?: English
    }
}
