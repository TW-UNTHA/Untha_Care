package com.untha.view.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.view.activities.MainActivity
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.horizontalProgressBar
import org.jetbrains.anko.imageView
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class SingleSelectionQuestionFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view as _LinearLayout) {
            verticalLayout {
                verticalLayout {
                    loadHorizontalProgressBar(Constants.TEMPORAL_LOAD_PROGRESS_BAR)
                }
                verticalLayout {
                    loadImageAudio(view)
                }

                verticalLayout {
                    question()
                }
                verticalLayout {
                    option()
                }
            }.lparams {
                margin = dip(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
            }
        }
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

    private fun _LinearLayout.loadHorizontalProgressBar(loadProgress: Int) {

        verticalLayout {
            horizontalProgressBar {
                progress = loadProgress
                progressDrawable = ContextCompat.getDrawable(
                    context, R.drawable.progress_bar
                )
            }.lparams(width = matchParent)
        }
    }

    private fun _LinearLayout.loadImageAudio(view: View) {

        imageView {
            gravity = Gravity.CENTER
            Glide.with(view)
                .load(R.drawable.icon_question_audio)
                .into(this)
        }.lparams(width = Constants.SIZE_IMAGE_AUDIO_ROUTE, height = Constants.SIZE_IMAGE_AUDIO_ROUTE) {
            topMargin = dip(Constants.SIZE_IMAGE_AUDIO_ROUTE)
        }

    }

    private fun _LinearLayout.question() {
        verticalLayout {
            textView {
                gravity = Gravity.CENTER
                text = "¿Qué tipo de jornada de trabajo tienes?"
            }.lparams(width = wrapContent, height = wrapContent) {
                margin = dip(Constants.MARGIN_QUESTION_ROUTE)
            }
        }
    }

    private fun _LinearLayout.option() {

        themedButton(theme = R.style.MyButtonStyle){
            text="Tiempo completo: 40 horas"
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
            allCaps = false
            onClick { /* Todo on click */}
        }.lparams(width = matchParent)


    }
}
