package com.untha.view.fragments

import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.untha.model.transactionalmodels.Category
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent

fun _LinearLayout.loadImageNestStep(view: View, categoryNextStep: Category) {
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

