package com.untha.view.extension

import androidx.core.content.ContextCompat
import com.untha.R
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.horizontalProgressBar
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

fun _LinearLayout.loadHorizontalProgressBar(loadProgress: Int) {
    verticalLayout {
        horizontalProgressBar {
            progress = loadProgress
            progressDrawable = ContextCompat.getDrawable(
                context, R.drawable.progress_bar
            )
        }.lparams(width = matchParent)
    }
}
