package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.PixelConverter
import com.untha.view.activities.MainActivity
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._ScrollView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


class CalculatorsFragment : BaseFragment() {
    private var calculatorsRoutes: List<Category>? = null
    private lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        calculatorsRoutes = bundle?.get(Constants.CATEGORIES_CALCULATORS) as List<Category>
    }

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
        with(view as _ScrollView) {
            verticalLayout {
                buildCalculator(view)
            }.lparams(width = matchParent, height = matchParent)

        }
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_CALCULATOR,
            enableCustomBar = false,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null

        )

    }

    private fun createMainLayout(): View {
        return UI {
            scrollView {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildCalculator(view: View) {
        calculatorsRoutes?.map { calculator ->
            logAnalyticsSelectContentWithId(
                "${Constants.CLICK_ROUTE_TITLE}${calculator.title}", ContentType.ROUTE
            )
            verticalLayout {
                isClickable = true
                loadTitleCalculator(calculator)
                loadSubtitleCalculator(calculator)
                loadImageCalculator(view, calculator)
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )

            }.lparams(matchParent, calculateHeightRoute()) {
                topMargin = calculateTopMargin()
                rightMargin = calculateLateralMargin() - dip(Constants.SHADOW_PADDING_SIZE)
                leftMargin = calculateLateralMargin()
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadTitleCalculator(
        calculator: Category
    ) {
        textView {
            text = calculator.title
            textSizeDimen = R.dimen.text_size
            textColor =
                ContextCompat.getColor(context, R.color.colorTitleCategoryRoute)
            setTypeface(typeface, Typeface.BOLD)
        }.lparams {
            topMargin = calculateTopMargin()
            rightMargin = calculateLateralMargin()
            leftMargin = calculateLateralMargin()
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadSubtitleCalculator(
        calculator: Category
    ) {
        textView {
            text = calculator.subtitle
            textSizeDimen = R.dimen.text_size_content_for_many_characters
            textColor =
                ContextCompat.getColor(context, R.color.colorTitleCategoryRoute)
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_light
            )
        }.lparams {
            topMargin = calculateTopMargin()
            rightMargin = calculateLateralMargin()
            leftMargin = calculateLateralMargin()
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImageCalculator(
        view: View,
        category: Category
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent) {
            topMargin = calculateTopMargin()
        }
    }

    private fun calculateTopMargin(): Int {
        val topMarginDps = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE_MAIN_ROUTE

        return PixelConverter.toPixels(topMarginDps, context)

    }

    private fun calculateLateralMargin(): Int {
        val cardWidthInDps =
            PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_LATERAL_PERCENTAGE_MAIN_ROUTE
        return PixelConverter.toPixels(cardWidthInDps, context)
    }

    private fun calculateHeightRoute(): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * Constants.SIZE_ROUTE_CATEGORY

        return PixelConverter.toPixels(cardHeightInDps, context)
    }
}
