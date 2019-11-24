package com.untha.view.extension

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
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

    }
}

fun @AnkoViewDslMarker _LinearLayout.buildNextStepTitle(categoryNextStep: Category) {
    textView {
        val title = categoryNextStep.titleNextStep
        textSizeDimen = R.dimen.text_size_content_next_step
        gravity = Gravity.START
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

fun @AnkoViewDslMarker _LinearLayout.buildImageNextStep(
    view: View,
    category: Category
) {
    val widthImageDp =
        (PixelConverter.getScreenDpWidth(context)) * Constants.WIDTH_IMAGE
    val widthImagePixel =
        PixelConverter.toPixels(
            widthImageDp,
            context
        )
    linearLayout {
        padding = dip(Constants.PERCENTAGE_PADDING_ELEMENT_NEXT_STEP_IMAGE)
        loadImageNextStep(view, category)
    }.lparams(
        width = widthImagePixel
    )
}

fun @AnkoViewDslMarker _LinearLayout.getSelectableItemBackground(): TypedValue {
    val outValue = TypedValue()
    context.theme.resolveAttribute(
        android.R.attr.selectableItemBackground,
        outValue,
        true
    )

    return outValue
}





