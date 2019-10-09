package com.untha.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import org.jetbrains.anko.windowManager

object PixelConverter {


    fun toPixels(dp: Double, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun getScreenDpHeight(context: Context?): Int {

        val displayMetrics = DisplayMetrics()
        context?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return (Constants.REFERENCE_DENSITY * displayMetrics.heightPixels) / getScreenDpiDensity(
            context
        )
    }

    fun getScreenDpWidth(context: Context?): Int {
        val displayMetrics = DisplayMetrics()
        context?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return (Constants.REFERENCE_DENSITY * displayMetrics.widthPixels) / getScreenDpiDensity(
            context
        )
    }

    private fun getScreenDpiDensity(context: Context?): Int {
        val displayMetrics = DisplayMetrics()
        context?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.densityDpi
    }
}
