package com.untha.view.fragments

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView

fun _LinearLayout.loadImageNextStep(view: View, categoryNextStep: Category) {
    imageView {

        Gravity.LEFT

        val imageUrl = resources.getIdentifier(
            categoryNextStep?.image,
            "drawable",
            context.applicationInfo.packageName
        )
        Glide.with(view)
            .load(imageUrl)
            .into(this)

    }.lparams(
    )
}

fun @AnkoViewDslMarker _LinearLayout.loadImage(view: View, category: Category) {
    imageView {
        val imageUrl = resources.getIdentifier(
            category.information?.get(0)?.image,
            "drawable",
            context.applicationInfo.packageName
        )
        Glide.with(view)
            .load(imageUrl)
            .into(this)

    }.lparams(width = matchParent, height = matchParent)
}

fun @AnkoViewDslMarker _LinearLayout.buildNextStepTitle(categoryNextStep: Category) {
    textView {
        val title = categoryNextStep.title
        textSizeDimen = R.dimen.text_size_content
        gravity = Gravity.LEFT
        text = title
        textColor =
            ContextCompat.getColor(
                context,
                R.color.colorGenericTitle
            )

        setTypeface(typeface, Typeface.NORMAL)
    }
}


fun @AnkoViewDslMarker _LinearLayout.loadImage(
    view: View,
    imageHeight: Int,
    category: Category

) {
    imageView {
        val imageUrl = resources.getIdentifier(
            category.information?.get(0)?.image,
            "drawable",
            context.applicationInfo.packageName
        )
        Glide.with(view)
            .load(imageUrl)
            .into(this)
        scaleType = ImageView.ScaleType.FIT_XY
    }.lparams(width = matchParent, height = imageHeight)
}

