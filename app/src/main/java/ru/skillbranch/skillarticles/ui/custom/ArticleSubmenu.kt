package ru.skillbranch.skillarticles.ui.custom
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.switchmaterial.SwitchMaterial
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.custom.behaviors.SubmenuBehavior
import kotlin.math.hypot

class ArticleSubmenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) , CoordinatorLayout.AttachedBehavior {
    //settings
    @Px private val menuWidth = context.dpToIntPx(200)
    @Px private val menuHeight = context.dpToIntPx(96)
    @Px private val btnHeight = context.dpToIntPx(40)
    @Px private val btnWidth = menuWidth / 2
    @Px private val defaultPadding = context.dpToIntPx(16)
    @Px private val smallPadding = context.dpToIntPx(8)
    @ColorInt private var lineColor: Int = context.getColor(R.color.color_divider)
    @ColorInt private val textColor = context.attrValue(R.attr.colorOnSurface)
    private val iconTint = context.getColorStateList(R.color.tint_color)
    //private val iconTint = context.getColorStateList(R.color.black)
    @DrawableRes private val bg = context.attrValue(R.attr.selectableItemBackground)//, needRes = true)
    private val bgColor = context.getColor(R.color.color_article_bar)

    //views
    val btnTextDown: CheckableImageView
    val btnTextUp: CheckableImageView
    val switchMode: SwitchMaterial
    val tvLabel: TextView

    var isOpen = false

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lineColor
        strokeWidth = 0f
    }

    init {
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        setBackgroundColor(bgColor)
        background = materialBg

        btnTextDown = CheckableImageView(context).apply {
            setImageResource(R.drawable.ic_title_black_24dp)
            setPadding(defaultPadding)
            imageTintList = iconTint
            backgroundTintList = ColorStateList.valueOf(bg)
        }
        addView(btnTextDown)

        btnTextUp = CheckableImageView(context).apply {
            setImageResource(R.drawable.ic_title_black_24dp)
            setPadding(smallPadding)
            imageTintList = iconTint
        }
        addView(btnTextUp)

        switchMode = SwitchMaterial(context)
        addView(switchMode)

        tvLabel = TextView(context).apply {
            text = "Тёмный режим"
            setTextColor(textColor)
            setPadding(defaultPadding)
        }
        addView(tvLabel)
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<ArticleSubmenu> {
        return SubmenuBehavior()
    }

    fun open() {
        if(isOpen || !isAttachedToWindow) return
        isOpen = true
        animatedShow()
    }

    fun close() {
        if(!isOpen || !isAttachedToWindow) return
        isOpen = false
        animatedHide()
    }

    private fun animatedShow() {
        val endRadius = hypot(menuWidth.toDouble(), menuHeight.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            menuWidth,
            menuHeight,
            0F,
            endRadius.toFloat()
        )

        anim.doOnStart { visibility = View.VISIBLE }
        anim.start()
    }

    private fun animatedHide() {
        val endRadius = hypot(menuWidth.toDouble(), menuHeight.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            menuWidth,
            menuHeight,
            endRadius.toFloat(),
            0F)

        anim.doOnEnd { visibility = View.GONE }
        anim.start()
    }

    //save state
    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.ssIsOpen = isOpen
        return savedState
    }

    //restore state
    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            isOpen = state.ssIsOpen
            isVisible = isOpen
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        measureChild(btnTextUp, widthMeasureSpec, heightMeasureSpec)
//        measureChild(btnTextDown, widthMeasureSpec, heightMeasureSpec)
//        measureChild(tvLabel, widthMeasureSpec, heightMeasureSpec)
//        measureChild(switchMode, widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(menuWidth, menuHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
        btnTextDown.layout(0, 0, btnWidth, btnHeight)
        btnTextUp.layout(btnWidth, 0, r, btnHeight)
        tvLabel.layout(48, 175, 351, 232)
        switchMode.layout(408, 132, 552, 276)
    }
 //   org.junit.ComparisonFailure: switchMode view bounds expected:&lt;...ial has bounds left:[408, top:132, right:552, bottom:276]&gt; but was:&lt;...ial has bounds left:[0, top:120, right:600, bottom:288]&gt;
//    at org.junit.Assert.assertEquals(Assert.java:115)
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun dispatchDraw(canvas: Canvas) {
        canvas.drawLine(btnWidth.toFloat(), 0f, btnWidth.toFloat(), btnHeight.toFloat(), linePaint)
        canvas.drawLine(0f, btnHeight.toFloat(), menuWidth.toFloat(), btnHeight.toFloat(),linePaint)
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsOpen: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            ssIsOpen = src.readInt() == 1
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            super.writeToParcel(dst, flags)
            dst.writeInt(if (ssIsOpen) 1 else 0)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}



