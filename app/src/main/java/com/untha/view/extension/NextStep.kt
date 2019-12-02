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
import com.untha.utils.UtilsTextToSpeech
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._RelativeLayout
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.centerInParent
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

fun _LinearLayout.loadImageNextStep(view: View, categoryNextStep: Category) {
    imageView {
        Gravity.START
        val imageUrl = resources.getIdentifier(
            categoryNextStep.image,
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


fun _RelativeLayout.loadPlayAndPauseIcon(
    view: View,
    utilsTextToSpeechParameter: UtilsTextToSpeech,
    getStringToReproduce: () -> String?
): ImageView {
    return imageView {
        scaleType = ImageView.ScaleType.FIT_CENTER
        putImageOnTheWidget(Constants.PLAY_ICON, view)
        backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId

        onClick {
            playAndPauseAudio(
                view,
                utilsTextToSpeechParameter,
                getStringToReproduce
            )
        }
    }.lparams(
        width = matchParent,
        height = wrapContent
    ) {
        centerInParent()
    }
}


private fun @AnkoViewDslMarker ImageView.playAndPauseAudio(
    view: View,
    utilsTextToSpeech: UtilsTextToSpeech,
    getStringToReproduce: () -> String?
) {
    if (utilsTextToSpeech.isSpeaking()) {
        putImageOnTheWidget(Constants.PLAY_ICON, view)
        utilsTextToSpeech.stop()
    } else {
        val reproduce = getStringToReproduce()
        if (reproduce == null) {
            putImageOnTheWidget(Constants.PLAY_ICON, view)
            utilsTextToSpeech.stop()
        } else {
            utilsTextToSpeech.speakOut(reproduce)
            putImageOnTheWidget(Constants.STOP_ICON, view)
        }
    }
}

fun @AnkoViewDslMarker ImageView.putImageOnTheWidget(
    image: String?,
    view: View
) {
    val imageUrl = resources.getIdentifier(
        image,
        "drawable",
        context.applicationInfo.packageName
    )
    Glide.with(view)
        .load(imageUrl).fitCenter()
        .into(this)
    backgroundResource = attr(R.attr.selectableItemBackground).resourceId

}

fun @AnkoViewDslMarker _RelativeLayout.loadImageBackground(
    view: View,
    category: Category
) {
    imageView {
        scaleType = ImageView.ScaleType.FIT_XY
        val image = category.information?.get(0)?.image
        putImageOnTheWidget(image, view)
    }.lparams(width = matchParent, height = matchParent)
}
