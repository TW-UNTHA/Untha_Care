package com.untha.view.fragments

import android.content.Intent
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
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.view.activities.MainActivity
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._RelativeLayout
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class RoutesFragment : BaseFragment() {
    private var categoriesRoutes: List<Category>? = null
    private lateinit var mainActivity: MainActivity

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
        with(view as _RelativeLayout) {
            verticalLayout {
                buildRoute(view)
            }.lparams(width = matchParent, height = matchParent)
            loadShareFloatingButton()

        }
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_ROUTES,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = true,
            backMethod = ::navigateToCategory
        )
        goAboutHelp(view)
    }

    private fun goAboutHelp(view: View) {
        val layoutActionBar = (activity as MainActivity).supportActionBar?.customView
        val help = layoutActionBar?.findViewById(R.id.icon_help) as ImageView
        help.onClick {
            view.findNavController()
                .navigate(
                    R.id.trhAboutInstructions,
                    null,
                    navOptions,
                    null
                )
            logAnalyticsCustomContentTypeWithId(ContentType.HELP, FirebaseEvent.HELP)
        }
    }

    private fun navigateToCategory() {
        val bundle = Bundle().apply {
            putBoolean("showScreen", false)
        }
        NavHostFragment.findNavController(this)
            .navigate(R.id.categoryFragment, bundle, navOptionsToBackNavigation, null)
        logAnalyticsCustomContentTypeWithId(ContentType.CLOSE, FirebaseEvent.CLOSE)
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRoute(view: View) {
        categoriesRoutes?.map { route ->
            logAnalyticsSelectContentWithId(
                "${Constants.CLICK_ROUTE_TITLE}${route.title}", ContentType.ROUTE
            )
            verticalLayout {
                isClickable = true
                loadTitleRoute(route)
                loadImageRoute(view, route)
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )
                when (route.id) {
                    Constants.ID_ROUTE_LABOUR -> {
                        setOnClickListener {
                            onItemClickRouteLabour(view)
                        }
                    }
                    Constants.ID_ROUTE_VIOLENCE -> {
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

    private fun @AnkoViewDslMarker _LinearLayout.loadTitleRoute(
        route: Category
    ) {
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
            relativeLayout {
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
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent) {
            topMargin = calculateTopMargin()
        }
    }

    private fun @AnkoViewDslMarker _RelativeLayout.loadShareFloatingButton(
    ) {
        floatingActionButton {
            imageResource = R.drawable.ic_share
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.colorHeaderBackground)
            onClick {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_app))
                sendIntent.type = "text/plain"
                context.startActivity(sendIntent)
                logAnalyticsCustomContentTypeWithId(ContentType.SHARE, FirebaseEvent.SHARE)
            }
        }.lparams {
            margin = dip(Constants.SHARE_BUTTON_MARGIN)
            alignParentBottom()
            alignParentRight()
        }
    }
}
