package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.graphics.withTranslation
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior
import kotlin.math.hypot

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    //sizes
    @Px private val iconSize = context.dpToIntPx(56)
    @Px private val iconPadding = context.dpToIntPx(16)
    private val iconTint = context.getColorStateList(R.color.tint_color)
    private val bgColor = context.getColor(R.color.color_article_bar)

    //views
    val btnLike: CheckableImageView
    val btnBookmark: CheckableImageView
    val btnShare: AppCompatImageView
    val btnSettings: CheckableImageView

    private val searchBar: SearchBar
    val tvSearchResult
        get() = searchBar.tvSearchResult
    val btnResultUp
        get() = searchBar.btnResultUp
    val btnResultDown
        get() = searchBar.btnResultDown
    val btnSearchClose
        get() = searchBar.btnSearchClose

    var isSearchMode = false

     override fun getBehavior(): CoordinatorLayout.Behavior<Bottombar> {
         return BottombarBehavior()
     }

    init {
        searchBar = SearchBar()
        addView(searchBar)

        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        setBackgroundColor(bgColor)
        background = materialBg

        btnLike = CheckableImageView(context).apply {
            setImageResource(R.drawable.like_states)
            imageTintList = iconTint
            setPadding(iconPadding)
        }
        addView(btnLike)

        btnBookmark = CheckableImageView(context).apply {
            setImageResource(R.drawable.bookmark_states)
            imageTintList = iconTint
            setPadding(iconPadding)
        }
        addView(btnBookmark)

        btnShare= AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_share_black_24dp)
            imageTintList = iconTint
            setPadding(iconPadding)
        }
        addView(btnShare)

        btnSettings= CheckableImageView(context).apply {
            setImageResource(R.drawable.ic_format_size_black_24dp)
            imageTintList = iconTint
            setPadding(iconPadding)
        }
        addView(btnSettings)
    }

    override fun onSaveInstanceState(): Parcelable {
       val saveState = SavedState(super.onSaveInstanceState())
        saveState.ssIsSearchMode = isSearchMode
        return saveState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if(state is SavedState){
            isSearchMode = state.ssIsSearchMode
            searchBar.isVisible = isSearchMode
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = iconSize
        Log.d("Bottombar", "parentWidth = $parentWidth")
        Log.d("Bottombar", "Width = $width")
        setMeasuredDimension(parentWidth, height)
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d("Bottombar", "l = $l, t = $t, r = $r, b = $b")
        var usedWidth = 0
        val top = 0
        val bottom = iconSize

        if (isSearchMode) {
            searchBar.layout(0, 0, r, iconSize)
        } else {
            btnLike.layout(0, top, iconSize, bottom)
            usedWidth += iconSize

            btnBookmark.layout(usedWidth, top, usedWidth + iconSize, bottom)
            usedWidth += iconSize

            btnShare.layout(usedWidth, top, usedWidth + iconSize, bottom)

            btnSettings.layout(r - iconSize, top, r, bottom)
        }
    }

    fun setSearchState(isSearch: Boolean) {
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
        invalidate()
        if (isSearchMode) animatedShowSearch()
        else animateHideSearch()
     }

    fun setSearchInfo(searchCount: Int = 0, position: Int = 0) {
        btnResultUp.isEnabled = searchCount > 0
        btnResultDown.isEnabled = searchCount > 0

        tvSearchResult.text =
            if (searchCount == 0) "Not found" else "${position.inc()} of $searchCount"

        when (position) {
            0 -> btnResultUp.isEnabled = false
            searchCount.dec() -> btnResultDown.isEnabled = false
        }
    }


    private fun animatedShowSearch() {
        searchBar.isVisible = true
        btnLike.isVisible = false
        btnBookmark.isVisible = false
        btnShare.isVisible = false
        btnSettings.isVisible = false

        val endRadius = hypot(width.toDouble(), height / 2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            0f,
            endRadius.toFloat()
        )
//        va.doOnEnd { }
        va.start()
    }

    private fun animateHideSearch() {

        val endRadius = hypot(width.toDouble(), height / 2.toDouble())

        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            endRadius.toFloat(),
            0f
        )
        va.doOnEnd {
            btnLike.isVisible = true
            btnBookmark.isVisible = true
            btnShare.isVisible = true
            btnSettings.isVisible = true
            searchBar.isVisible = false
        }
        va.start()
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsSearchMode: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            ssIsSearchMode = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte(if (ssIsSearchMode) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

    @SuppressLint("ViewConstructor")
    inner class SearchBar : ViewGroup(context, null, 0) {
        internal val btnSearchClose: AppCompatImageView
        internal val tvSearchResult: TextView
        internal val btnResultDown: AppCompatImageView
        internal val btnResultUp: AppCompatImageView
        @ColorInt private val iconColor = context.attrValue(R.attr.colorPrimary)
        private val bgSColor = context.getColorStateList(R.color.color_on_article_bar)

        init {

            background = GradientDrawable().apply {
                color = bgSColor
            }

            btnSearchClose = AppCompatImageView(context).apply {
                setImageResource(R.drawable.ic_close_black_24dp)
                setPadding(iconPadding)
                imageTintList = ColorStateList.valueOf(iconColor)
                setOnClickListener { isSearchMode = false }
            }
            addView(btnSearchClose)

            tvSearchResult = TextView(context).apply {
                setTextColor(iconColor)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(iconPadding)
            }
            addView(tvSearchResult)

            btnResultDown = AppCompatImageView(context).apply {
                setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                setPadding(iconPadding)
                imageTintList = ColorStateList.valueOf(iconColor)
            }
            addView(btnResultDown)

            btnResultUp = CheckableImageView(context).apply {
                setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                setPadding(iconPadding)
                imageTintList = ColorStateList.valueOf(iconColor)
            }
            addView(btnResultUp)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            val height = iconSize
            setMeasuredDimension(width, height)
        }

        override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {

            btnSearchClose.layout(0, 0, iconSize, iconSize)
            tvSearchResult.layout(iconSize, 0, r - 2*iconSize, iconSize)
            btnResultDown.layout(r - 2*iconSize, 0, r - iconSize, iconSize)
            btnResultUp.layout(r - iconSize, 0, r, iconSize)
        }

    }
}

