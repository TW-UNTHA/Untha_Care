package com.untha.view.extension

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
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
import org.jetbrains.anko.backgroundColor
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


fun _RelativeLayout.loadIconButtonPlayAndPause(
    view: View,
    informationToSpeech: String,
    utilsTextToSpeechParameter: UtilsTextToSpeech?
) {
    imageView {
        var utilsTextToSpeech = utilsTextToSpeechParameter

        scaleType = ImageView.ScaleType.FIT_CENTER
        putImageOnTheWidget("ic_play_audio", view)
        backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
        var textCategory = informationToSpeech
        textCategory = textCategory.parseAsHtml().toString()
        val listParagraph: MutableList<String> = mutableListOf()
        val separated = textCategory.split(".")
        separated?.mapIndexed { index, item ->
            if (!item.equals("")) {
                listParagraph.add(index, item)
            }
        }
        val indexParameter = 0

        var index = indexParameter
        fun a(): String {
            index++
            indexCurrently = index
            if (index < listParagraph.size) {
                println("index${index}")
                return listParagraph[index]
            } else {
                utilsTextToSpeech?.stop()
                return ""
            }
        }
        utilsTextToSpeech = UtilsTextToSpeech(context!!, ::a)
        onClick {

            if (index == Constants.INIT_SPEAK) {
                index = indexParameter
            }
            if (ban == Constants.GET_CURRENT_INDEX) {
                index = indexCurrently
                ban = Constants.INIT_SPEAK
            }
            if (ban == Constants.STOP_SPEAK) {
                putImageOnTheWidget("ic_stop_audio", view)
                utilsTextToSpeech?.stop()
                ban = Constants.GET_CURRENT_INDEX
            }
            if (ban == Constants.INIT_SPEAK) {
                utilsTextToSpeech.speakOut(listParagraph[index], null)
                putImageOnTheWidget("ic_play_audio", view)
                ban = Constants.STOP_SPEAK
            }
        }
    }.lparams(
        width = matchParent,
        height = matchParent
    ) {
        centerInParent()
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
}

var indexCurrently = 0
var ban: Int = 0

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







