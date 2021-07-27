package ru.skillbranch.skillarticles.ui.custom
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.os.Parcel
//import android.os.Parcelable
//import android.util.AttributeSet
//import android.util.Log
//import android.view.View
//import android.view.ViewAnimationUtils
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.annotation.*
//import androidx.coordinatorlayout.widget.CoordinatorLayout
//import androidx.core.animation.doOnEnd
//import androidx.core.animation.doOnStart
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import com.google.android.material.shape.MaterialShapeDrawable
//import com.google.android.material.switchmaterial.SwitchMaterial
//import ru.skillbranch.skillarticles.R
//import ru.skillbranch.skillarticles.extensions.attrValue
//import ru.skillbranch.skillarticles.extensions.dpToIntPx
//import ru.skillbranch.skillarticles.ui.custom.behaviors.SubmenuBehavior
//import kotlin.math.hypot
//
//class ArticleSubmenu @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : ViewGroup(context, attrs, defStyleAttr) , CoordinatorLayout.AttachedBehavior {
//    //settings
//    @Px private val menuWidth = context.dpToIntPx(200)
//    @Px private val menuHeight = context.dpToIntPx(96)
//    @Px private val btnHeight = context.dpToIntPx(40)
//    @Px private val btnWidth = menuWidth / 2
//    @Px private val defaultPadding = context.dpToIntPx(16)
//    @ColorInt private var lineColor: Int = context.getColor(R.color.color_divider)
//    @ColorInt private val textColor = context.attrValue(R.attr.colorOnSurface)
//    private val iconTint = context.getColorStateList(R.color.tint_color)
// //   @DrawableRes private val bg = context.attrValue(R.attr.selectableItemBackground, needRes = true)
//
//    //views
//    val btnTextDown: CheckableImageView
//    val btnTextUp: CheckableImageView
//    val switchMode: SwitchMaterial
//    val tvLabel: TextView
//
//    var isOpen = true
//
//    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = lineColor
//        strokeWidth = 0f
//    }
//
//    init {
//        btnTextDown
//        btnTextUp
//        switchMode
//        tvLabel
//    }
//
//    override fun getBehavior(): CoordinatorLayout.Behavior<ArticleSubmenu> {
//        return SubmenuBehavior()
//    }
//
//    fun open() {
//        //TODO implement me
//    }
//
//    fun close() {
//        //TODO implement me
//    }
//
//    private fun animatedShow() {
//        //TODO implement me
//    }
//
//    private fun animatedHide() {
//        //TODO implement me
//    }
//
//   //save state
//    override fun onSaveInstanceState(): Parcelable? {
//        val savedState = SavedState(super.onSaveInstanceState())
//        savedState.ssIsOpen = isOpen
//        return savedState
//    }
//
//    //restore state
//    override fun onRestoreInstanceState(state: Parcelable) {
//        super.onRestoreInstanceState(state)
//        if (state is SavedState) {
//            isOpen = state.ssIsOpen
//            isVisible = isOpen
//        }
//    }
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
//    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        //TODO implement me
//    }
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
//    public override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        //TODO implement me
//    }
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
//    public override fun dispatchDraw(canvas: Canvas) {
//        //TODO implement me
//    }
//
//    private class SavedState : BaseSavedState, Parcelable {
//        var ssIsOpen: Boolean = false
//
//        constructor(superState: Parcelable?) : super(superState)
//
//        constructor(src: Parcel) : super(src) {
//            ssIsOpen = src.readInt() == 1
//        }
//
//        override fun writeToParcel(dst: Parcel, flags: Int) {
//            super.writeToParcel(dst, flags)
//            dst.writeInt(if (ssIsOpen) 1 else 0)
//        }
//
//        override fun describeContents() = 0
//
//        companion object CREATOR : Parcelable.Creator<SavedState> {
//            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
//            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
//        }
//    }
//
//}