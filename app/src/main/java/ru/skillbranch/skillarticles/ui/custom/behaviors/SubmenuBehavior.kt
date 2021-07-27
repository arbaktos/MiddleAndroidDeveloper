package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenuOld
import ru.skillbranch.skillarticles.ui.custom.BottombarOld


class SubmenuBehavior() : CoordinatorLayout.Behavior<ArticleSubmenuOld>() {
    constructor(context: Context, attrs: AttributeSet) : this()

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenuOld,
        dependency: View
    ): Boolean {
        return dependency is BottombarOld
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenuOld,
        dependency: View
    ): Boolean {

        return if (child.isOpen && dependency.translationY >= 0f) {
            animate(child, dependency)
            true
        } else false
    }

    private fun animate(child: View, dependency: View) {
        val fraction = dependency.translationY / dependency.height
        child.translationX = (child.width + child.marginRight) * fraction
    }
}