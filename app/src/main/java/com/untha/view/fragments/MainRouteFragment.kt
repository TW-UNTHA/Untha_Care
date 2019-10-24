package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
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

class MainRouteFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private var categoriesRoutes: List<Category>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        categoriesRoutes = bundle?.get(Constants.CATEGORIES_ROUTES) as List<Category>
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
        with(view as _LinearLayout) {
            verticalLayout {
                buildRoute(view)
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRoute(view: View) {
        categoriesRoutes?.map {
            firebaseAnalytics.setCurrentScreen(activity!!, Constants.CLICK_ROUTE_TITLE+it.title, null)
            verticalLayout {
                isClickable=true
                textView {
                    text = it.title
                    textSizeDimen = R.dimen.text_size
                    textColor =
                    ContextCompat.getColor(context, R.color.colorTitleCategoryRoute)
                    setTypeface(typeface, Typeface.BOLD)
                }.lparams{
                    topMargin=calculateTopMargin()
                    rightMargin=calculateLateralMargin()
                    leftMargin=calculateLateralMargin()
                    weight = 1.0F
                }

                loadImageRoute( view,it)
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )

            }.lparams(matchParent, calculateHeightRoute()){
                topMargin=calculateTopMargin()
                rightMargin=calculateLateralMargin() - dip(Constants.SHADOW_PADDING_SIZE)
                leftMargin=calculateLateralMargin()
                weight = 1.0F
            }
        }

    }
   private fun calculateHeightRoute(): Int{
       val cardHeightInDps =
           (PixelConverter.getScreenDpHeight(context) -
                   Constants.SIZE_OF_ACTION_BAR_ROUTE) * Constants.SIZE_ROUTE_CATEGORY
       val height = context?.let { PixelConverter.toPixels(cardHeightInDps, it) }
       return height!!
   }

    private fun calculateTopMargin(): Int{
        val topMarginDps = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE_MAIN_ROUTE

        val marginTop = context?.let { PixelConverter.toPixels(topMarginDps, it) }
        return marginTop!!

    }
    private fun calculateLateralMargin(): Int{
        val cardWidthInDps =
            PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_LATERAL_PERCENTAGE_MAIN_ROUTE

        val marginLateral = context?.let { PixelConverter.toPixels(cardWidthInDps, it) }
        return marginLateral!!

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
    )
    {
        imageView {
            val imageUrl = resources.getIdentifier(
                category?.image,
                "drawable",
                context.applicationInfo.packageName
            )
            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent){
           // topMargin=calculateTopMargin()
            weight = 1.0F
        }
    }

}
