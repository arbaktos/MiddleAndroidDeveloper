package ru.skillbranch.skillarticles.extensions


fun String?.indexesOf(
    substr: String,
    ignoreCase: Boolean = true
): List<Int> {
    return if (this != null && substr.isNotEmpty()) {
        substr.toRegex(ignoreCaseOpt(ignoreCase))
            .findAll(this)
            .map { it.range.first }
            .toList()
    } else emptyList()
}

fun ignoreCaseOpt(ignoreCase: Boolean) =
    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()