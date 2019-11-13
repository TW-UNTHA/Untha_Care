package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.PixelConverter
import com.untha.view.activities.MainActivity
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class RoutesFragment : BaseFragment() {
    private var categoriesRoutes: List<Category>? = null
    private lateinit var mainActivity: MainActivity

    companion object {
        const val ROUTE_LABOUR = 14
        const val ROUTE_VIOLENCE = 15
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigateToCategory()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        categoriesRoutes = bundle?.get(Constants.CATEGORIES_ROUTES) as List<Category>
        activity?.onBackPressedDispatcher?.addCallback(this, callback)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return createMainLayout()
    }

    private fun onItemClickRouteLabour(itemView: View) {
        val routeLabour = Bundle().apply {
            putString(Constants.TYPE_ROUTE, Constants.ROUTE_LABOUR)
        }
        itemView.findNavController()
           .navigate(R.id.mainScreenLabourRouteFragment, routeLabour, navOptions, null)
    }

    private fun onItemClickRouteViolence(itemView: View) {
        val violenceLabour = Bundle().apply {
            putString(Constants.TYPE_ROUTE, Constants.ROUTE_VIOLENCE)
        }
        itemView.findNavController()
            .navigate(R.id.mainScreenLabourRouteFragment, violenceLabour, navOptions, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.ROUTES_PAGE,
                null
            )
        }
        with(view as _LinearLayout) {
            verticalLayout {
                buildRoute(view)
            }
        }
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_ROUTES,
            enableCustomBar = false,
            needsBackButton = true,
            backMethod = ::navigateToCategory
        )
    }

    private fun navigateToCategory() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.categoryFragment, null, navOptionsToBackNavigation, null)
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRoute(view: View) {
        categoriesRoutes?.map { route ->
            logAnalyticsSelectContentWithId(
                "${Constants.CLICK_ROUTE_TITLE}${route.title}", ContentType.ROUTE
            )
            verticalLayout {
                isClickable = true
                textView {
                    text = route.title
                    textSizeDimen = R.dimen.text_size
                    textColor =
                        ContextCompat.getColor(context, R.color.colorTitleCategoryRoute)
                    setTypeface(typeface, Typeface.BOLD)
                }.lparams {
                    topMargin = calculateTopMargin()
                    rightMargin = calculateLateralMargin()
                    leftMargin = calculateLateralMargin()
                }

                loadImageRoute(view, route)
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )
                when (route.id) {
                    ROUTE_LABOUR -> {
                        setOnClickListener {
                            onItemClickRouteLabour(view)
                        }
                    }
                    ROUTE_VIOLENCE -> {
                        setOnClickListener {
                            onItemClickRouteViolence(view)
                        }
                    }
                }

            }.lparams(matchParent, calculateHeightRoute()) {
                topMargin = calculateTopMargin()
                rightMargin = calculateLateralMargin() - dip(Constants.SHADOW_PADDING_SIZE)
                leftMargin = calculateLateralMargin()
            }
        }

    }

    private fun calculateHeightRoute(): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * Constants.SIZE_ROUTE_CATEGORY

        return PixelConverter.toPixels(cardHeightInDps, context)
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

    private fun createMainLayout(): View {
        return UI {
            verticalLayout {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImageRoute(
        view: View,
        category: Category
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent)
    }
}
