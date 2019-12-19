package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.untha.R
import com.untha.utils.Constants
import com.untha.view.activities.MainActivity
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


class CalculatorFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
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
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_CALCULATOR,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = true,
            backMethod = null
        )
        with(view as _LinearLayout) {
            verticalLayout {
                orientation = LinearLayout.VERTICAL
                textView {
                    text = "calculadora"
                    backgroundColor =
                        ContextCompat.getColor(context, R.color.colorDotSlice)
                    textSizeDimen = R.dimen.text_size_question_route
                    typeface = ResourcesCompat.getFont(
                        context.applicationContext,
                        R.font.proxima_nova_light
                    )
                }.lparams(width = wrapContent, height = wrapContent)
//
//                textView {
//                    text = "newCalculadora"
//                    backgroundColor =
//                        ContextCompat.getColor(context, R.color.link)
//                    textSizeDimen = R.dimen.text_size_question_route
//                    typeface = ResourcesCompat.getFont(
//                        context.applicationContext,
//                        R.font.proxima_nova_light
//                    )
//                }.lparams(width = wrapContent, height = wrapContent)
//
//                textView {
//                    text = "Trabajadoras remuneradas del hogar"
//                    backgroundColor =
//                        ContextCompat.getColor(context, R.color.colorShapeCategoryRoute)
//                    textSizeDimen = R.dimen.text_size_question_route
//                    typeface = ResourcesCompat.getFont(
//                        context.applicationContext,
//                        R.font.proxima_nova_light
//                    )
//                }.lparams(width = wrapContent, height = wrapContent)
//                linearLayout {
//                    backgroundColor =
//                        ContextCompat.getColor(context, R.color.colorDotSlice)
//
//                    textView {
//
//                        text = "nuevoTrabajadoras remuneradas del hogar"
//                        backgroundColor =
//                            ContextCompat.getColor(context, R.color.colorProgressBarTint)
//                        textSizeDimen = R.dimen.text_size_question_route
//                        typeface = ResourcesCompat.getFont(
//                            context.applicationContext,
//                            R.font.proxima_nova_light
//                        )
//                    }.lparams(width = wrapContent, height = wrapContent) {
////                        padding = dip(20)
//                    }
//                }
            }
        }
    }
}
