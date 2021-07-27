package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
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
    val binding: LayoutBottombarBinding

     override fun getBehavior(): CoordinatorLayout.Behavior<Bottombar> {
         return BottombarBehavior()
     }

    init {
        binding = LayoutBottombarBinding.inflate(LayoutInflater.from(context), this)
        searchBar = SearchBar()
        btnLike = CheckableImageView(context).apply {
            setImageResource(R.drawable.like_states)
            imageTintList = iconTint
        }
        addView(btnLike)

        btnBookmark = CheckableImageView(context).apply {
            setImageResource(R.drawable.bookmark_states)
            imageTintList = iconTint
        }
        addView(btnBookmark)

        btnShare= CheckableImageView(context).apply {
            setImageResource(R.drawable.ic_share_black_24dp)
            imageTintList = iconTint
        }
        addView(btnShare)

        btnSettings= CheckableImageView(context).apply {
            setImageResource(R.drawable.ic_format_size_black_24dp)
            imageTintList = iconTint
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
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = iconSize + 2 * iconPadding
        setMeasuredDimension(width, height)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedWidth = iconPadding
        val bodyWidth = r - l - paddingLeft - paddingRight
        val top = t - iconPadding
        val bottom = b + iconPadding

        btnLike.layout(
            iconPadding,
            top,
            usedWidth + btnLike.measuredWidth,
            bottom
        )
        usedWidth += btnLike.measuredWidth + iconPadding

        btnBookmark.layout(
            iconPadding,
            top,
            usedWidth + btnBookmark.measuredWidth,
            bottom
        )
        usedWidth += btnLike.measuredWidth + iconPadding

        btnShare.layout(
            iconPadding,
            top,
            usedWidth + btnShare.measuredWidth,
            bottom
        )

        btnSettings.layout(
            r - iconPadding - btnSettings.measuredWidth,
            top,
            r - iconPadding,
            bottom
        )
    }

    fun setSearchState(isSearch: Boolean) {
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
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
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            0f,
            endRadius.toFloat()
        )
        va.doOnEnd {
            this.isVisible = false
        }
        va.start()
    }

    private fun animateHideSearch() {
        this.isVisible = true

        val endRadius = hypot(width.toDouble(), height / 2.toDouble())

        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            endRadius.toFloat(),
            0f
        )
        va.doOnEnd {
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

        init {
            btnSearchClose = AppCompatImageView(context).apply {
                setImageResource(R.drawable.ic_close_black_24dp)
                setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY)
                setOnClickListener { isSearchMode = false }
            }
            addView(btnSearchClose)

            tvSearchResult = TextView(context).apply {
                setTextColor(resources.getColor(R.color.color_primary))
            }
            addView(tvSearchResult)

            btnResultDown = AppCompatImageView(context).apply {
                setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY)
            }
            addView(btnResultDown)

            btnResultUp = CheckableImageView(context).apply {
                setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY)
            }
            addView(btnResultUp)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            val height = iconSize + 2 * iconPadding
            setMeasuredDimension(width, height)
        }

        override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
            btnSearchClose.layout(
                l + iconColor,
                t + iconPadding,
                l + iconSize + iconPadding,
                b - iconPadding
            )
            tvSearchResult
            btnResultDown
            btnResultUp
        }

    }
}

