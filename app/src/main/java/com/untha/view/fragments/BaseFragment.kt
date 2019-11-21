package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.untha.R
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.viewmodels.BaseQuestionViewModel

open class BaseFragment : Fragment() {

    var textToSpeech: TextToSpeech? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics
    val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
        .setPopEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_left)
        .setPopExitAnim(R.anim.slide_out_right).build()

    val navOptionsToBackNavigation = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
        .setPopEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_right)
        .setPopExitAnim(R.anim.slide_out_left).build()


    override fun onStop() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            firebaseAnalytics = FirebaseAnalytics.getInstance(it)
        }
    }


    fun logAnalyticsSelectContentWithId(name: String, contentType: ContentType) {
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
        val isSingle = questionGoToInfo["isSingle"] as Boolean
        val isLabourRoute = questionGoToInfo["isLabourRoute"] as Boolean
        val questionAdvance = questionGoToInfo["questionAdvance"] as Int

        when (goTo) {
            -1 -> {
                navigateToResultsScreen(isLabourRoute, view, questionViewModel)
            }
            else -> {
                val goToBundle: Bundle =
                    defineBundleData(isLabourRoute, goTo, route, questionAdvance)

                navigate(isSingle, view, goToBundle)
            }
        }
    }

    private fun defineBundleData(
        isLabourRoute: Boolean,
        goTo: Any?,
        route: Route,
        questionAdvance: Int
    ): Bundle {
        return when {
            isLabourRoute -> Bundle().apply {
                putInt(Constants.ROUTE_QUESTION_GO_TO, goTo as Int)
                putSerializable(Constants.ROUTE_LABOUR, route)
                putInt(Constants.QUESTION_ADVANCE, questionAdvance)
            }
            else -> Bundle().apply {
                putInt(Constants.ROUTE_QUESTION_GO_TO, goTo as Int)
                putSerializable(Constants.ROUTE_VIOLENCE, route)
                putInt(Constants.QUESTION_ADVANCE, questionAdvance)
            }
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
        isLabourRoute: Boolean,
        view: View, viewModel: BaseQuestionViewModel
    ) {
        viewModel.saveCompleteRouteResult(isLabourRoute)
        val bundle = Bundle().apply {
            putBoolean(Constants.IS_LABOUR_ROUTE, isLabourRoute)
        }
        view.findNavController().navigate(
            R.id.routeResultsFragment, bundle,
            navOptions, null
        )
    }
}

