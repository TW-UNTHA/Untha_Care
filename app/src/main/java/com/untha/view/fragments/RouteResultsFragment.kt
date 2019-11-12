package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.viewmodels.RouteResultsViewModel
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel

class RouteResultsFragment : BaseFragment() {

    companion object {
        private const val IMAGE_VIEW_WIDTH = 0.056
        private const val IMAGE_VIEW_HEIGHT = 0.023
        private const val IMAGE_VIEW_RIGHT_MARGIN = 0.025
        private const val BUTTON_CONTAINER_HEIGHT = 0.05
        private const val CONTAINER_HEIGHT = 0.164
    }

    private val viewModel: RouteResultsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.textToSpeech = TextToSpeech(context, this)
        return createMainLayout()
    }

    private fun createMainLayout(
    ): View {
        return UI {
            verticalLayout {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.ROUTE_RESULT_PAGE,
                null
            )
        }
        with(view as _LinearLayout) {
            linearLayout {
                viewModel.routeResults?.map { result ->
                    textView {
                        text = result.content
                    }

                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        imageView {
                            val imageUrl = resources.getIdentifier(
                                "result_category_icon",
                                "drawable",
                                context.applicationInfo.packageName
                            )
                            Glide.with(view)
                                .load(imageUrl)
                                .into(this)
                        }.lparams(
                            width = dip(calculateComponentsWidth(IMAGE_VIEW_WIDTH)),
                            height = dip(calculateComponentsHeight(IMAGE_VIEW_HEIGHT))
                        ) {
                            rightMargin = dip(calculateComponentsWidth(IMAGE_VIEW_RIGHT_MARGIN))
                        }

                        textView {
                            text = "Dummy text text"
                        }.lparams(width = wrapContent, height = matchParent)
                    }.lparams(
                        width = wrapContent,
                        height = dip(calculateComponentsHeight(BUTTON_CONTAINER_HEIGHT))
                    )
                }
            }.lparams(
                width = matchParent, height =
                dip(calculateComponentsHeight(CONTAINER_HEIGHT))
            ) {

            }
        }
    }

    private fun calculateComponentsHeight(heightComponent: Double): Float {
        return ((PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR_ROUTE) * heightComponent).toFloat()
    }

    private fun calculateComponentsWidth(widthComponent: Double): Float {
        return (PixelConverter.getScreenDpWidth(context) * widthComponent).toFloat()
    }
}
