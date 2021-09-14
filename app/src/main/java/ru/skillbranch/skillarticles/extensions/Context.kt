package ru.skillbranch.skillarticles.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlin.reflect.KProperty
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.delegates.AttrValue
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty


fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            this.resources.displayMetrics
    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            this.resources.displayMetrics
    ).toInt()
}

fun Context.attrValue(@AttrRes res: Int, needRes: Boolean = false): Int  {
    val value: Int?
    val tv = TypedValue()
    val resolveAttribute = this.theme.resolveAttribute(res, tv, true)
    if(resolveAttribute) value = if (needRes) tv.resourceId else tv.data
    else throw Resources.NotFoundException("Resource with id $res not found_extension")
    return value
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

@Suppress("DEPRECATION")
val Context.isNetworkAvailable: Boolean
    get() {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork?.run {
                val nc = cm.getNetworkCapabilities(this)
                nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                )
            } ?: false
        } else {
            cm.activeNetworkInfo?.run { isConnectedOrConnecting } ?: false
        }
    }