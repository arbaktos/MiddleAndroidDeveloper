package ru.skillbranch.skillarticles.extensions.data
import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel


    fun ArticleViewModel.ArticleState.toAppSettings() : AppSettings {
        return AppSettings(isDarkMode, isBigText)
    }

    fun ArticleViewModel.ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
        return ArticlePersonalInfo(isLike, isBookmark)
    }





