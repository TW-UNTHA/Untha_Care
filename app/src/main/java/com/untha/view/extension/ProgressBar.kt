package com.untha.view.extension

import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.untha.R
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.horizontalProgressBar
import org.jetbrains.anko.verticalLayout

fun _LinearLayout.loadHorizontalProgressBar(loadProgress: Int): LinearLayout {
    return verticalLayout {
        horizontalProgressBar {
            progress = loadProgress
            progressDrawable = ContextCompat.getDrawable(
                context, R.drawable.progress_bar
            )
        }
    }
}

fun _LinearLayout.loadHorizontalProgressBarDinamic(loadProgress: Int): ProgressBar {
    return horizontalProgressBar {
        progress = loadProgress
        progressDrawable = ContextCompat.getDrawable(
            context, R.drawable.progress_bar
        )
    }
}




