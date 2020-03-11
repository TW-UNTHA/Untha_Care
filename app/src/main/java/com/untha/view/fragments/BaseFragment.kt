package com.untha.view.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.BaseQuestionViewModel
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*
import kotlin.collections.ArrayList

open class BaseFragment : Fragment() {

    var textToSpeech: UtilsTextToSpeech? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics
    val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
        .setPopEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_left)
        .setPopExitAnim(R.anim.slide_out_right).build()

    val navOptionsToBackNavigation = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
        .setPopEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_right)
        .setPopExitAnim(R.anim.slide_out_left).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            firebaseAnalytics = FirebaseAnalytics.getInstance(it)
        }
        (activity as MainActivity).isLastScreen = ::isLastScreen
        (activity as MainActivity).isRouteResultsScreen = ::isRouteResultScreen
    }

    open fun isLastScreen(): Boolean {
        return false
    }

    open fun isRouteResultScreen(): Boolean {
        return false
    }

    fun logAnalyticsSelectContentWithId(name: String?, contentType: ContentType) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType.description)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logAnalyticsCustomEvent(event: FirebaseEvent) {
        firebaseAnalytics.logEvent(event.description, null)
    }

    fun logAnalyticsCustomEvent(event: String) {
        firebaseAnalytics.logEvent(event, null)
        firebaseAnalytics.logEvent(event, null)
    }

    fun logAnalyticsCustomContentTypeWithId(contentType: ContentType, event: FirebaseEvent) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType.description)
        firebaseAnalytics.logEvent(event.description, bundle)
    }


    fun manageGoToQuestion(
        questionGoToInfo: Map<String, Any?>,
        route: Route,
        view: View,
        questionViewModel: BaseQuestionViewModel
    ) {
        val goTo = questionGoToInfo["goTo"]
        val typeRoute = questionGoToInfo["getTypeRoute"] as String
        val isSingle = questionGoToInfo["isSingle"] as Boolean
        val questionAdvance = questionGoToInfo["questionAdvance"] as Int

        when (goTo) {
            -1 -> {
                if (typeRoute == Constants.ROUTE_CALCULATOR) {
                    val categoriesCalculator =
                        questionGoToInfo["CATEGORIES_CALCULATORS"] as ArrayList<Category>
                    val categoriesBundle = Bundle().apply {
                        putSerializable(Constants.CATEGORIES_CALCULATORS, categoriesCalculator)
                    }
                    view.findNavController().navigate(
                        R.id.calculatorFiniquitoFragment,
                        categoriesBundle,
                        navOptions, null
                    )
                } else {
                    navigateToResultsScreen(typeRoute, view, questionViewModel)
                }
            }
            else -> {
                val goToBundle: Bundle =
                    defineBundleData(
                        typeRoute,
                        goTo,
                        route,
                        questionAdvance,
                        questionGoToInfo
                    )

                navigate(isSingle, view, goToBundle)
            }
        }
    }

    private fun defineBundleData(
        typeRoute: String,
        goTo: Any?,
        route: Route,
        questionAdvance: Int,
        questionGoToInfo: Map<String, Any?>
    ): Bundle {
        var categoriesCalculator: ArrayList<Category>? = null

        if (questionGoToInfo["CATEGORIES_CALCULATORS"]!=null) {
            categoriesCalculator = questionGoToInfo["CATEGORIES_CALCULATORS"] as ArrayList<Category>
        }
        return Bundle().apply {
            putInt(Constants.ROUTE_QUESTION_GO_TO, goTo as Int)
            putSerializable(typeRoute, route)
            putInt(Constants.QUESTION_ADVANCE, questionAdvance)
            putSerializable(Constants.CATEGORIES_CALCULATORS, categoriesCalculator)
        }
    }

    private fun navigate(
        isSingle: Boolean,
        view: View,
        goToBundle: Bundle
    ) {
        if (isSingle) {
            view.findNavController().navigate(
                R.id.singleSelectQuestionFragment, goToBundle,
                navOptions, null
            )
        } else {
            view.findNavController().navigate(
                R.id.multipleSelectionQuestionFragment, goToBundle,
                navOptions, null
            )
        }
    }

    private fun navigateToResultsScreen(
        typeRoute: String,
        view: View, baseQuestionViewModel: BaseQuestionViewModel
    ) {
        baseQuestionViewModel.saveCompleteRouteResult(typeRoute)
        val bundle = Bundle().apply {
            putString(Constants.IS_LABOUR_ROUTE, typeRoute)
        }
        view.findNavController().navigate(
            R.id.routeResultsFragment, bundle,
            navOptions, null
        )
    }

    override fun onStop() {
        textToSpeech?.stop()
        super.onStop()
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        textToSpeech?.stop()
        super.onPause()
    }

    fun goBackMainScreenCategory(
        constantCategory: String, categories: ArrayList<Category>,
        idFragment: Int, view: View, mainActivity: MainActivity
    ) {
        val layoutActionBar = mainActivity.supportActionBar?.customView
        val categoriesType = Bundle().apply {
            putSerializable(
                constantCategory,
                categories
            )
        }
        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        close.onClick {
            view?.findNavController()
                ?.navigate(
                    idFragment,
                    categoriesType,
                    navOptionsToBackNavigation,
                    null
                )
            logAnalyticsCustomContentTypeWithId(ContentType.CLOSE, FirebaseEvent.CLOSE)
        }

    }
}

