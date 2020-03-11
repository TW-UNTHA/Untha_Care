package com.untha.view.fragments

import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.RoutesViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel

class MainScreenRoutesFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val routeViewModel: RoutesViewModel by viewModel()
    private var secondMessage: String = ""
    private var imageWelcome: String? = ""
    private var typeRoute: String? = ""
    private var isLabourRoute = false
    private var isThereLastResultForRoute = false

    companion object {
        const val ROUTE_BUTTON_TEXT = "Empezar"
        const val FIRST_MESSAGE = "¡Hola!"
        const val MARGINS = (((18 * 100) / 640) / 100F).toDouble()
        const val SPACE_TITLE_WITH_AUDIO = (((18 * 100) / 640) / 100F).toDouble()
        const val SPACE_LINK_BUTTON = (((20 * 100) / 640) / 100F).toDouble()
        const val SPACE_DESCRIPTION_WITH_TITLE = (((30 * 100) / 640) / 100F).toDouble()
        const val SIZE_SPACE_BUTTON_AND_IMAGE = 0.04375
        const val MARGIN_FOR_TOP_AND_BOTTOM = 0.5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            typeRoute = bundle.get(Constants.TYPE_ROUTE) as String
            isLabourRoute = typeRoute.equals(Constants.ROUTE_LABOUR)
        }
        loadMessagesRoute()
        isThereLastResultForRoute = routeViewModel.isThereLastResultForRoute(isLabourRoute)
    }

    private fun loadTitleRoute(isLabourRoute: Boolean) {
        val titleRoute =
            if (isLabourRoute) Constants.NAME_SCREEN_LABOUR_ROUTE else Constants.NAME_SCREEN_VIOLENCE_ROUTE
        (activity as MainActivity).customActionBar(
            titleRoute,
            enableCustomBar = false,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
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
        val sizeSpaceButtonAndImage = getHeightElementInDp(SIZE_SPACE_BUTTON_AND_IMAGE)
        val widthMainLayout =
            getHeightElementInDp(MARGINS)
        val marginMainLayout = PixelConverter.toPixels(widthMainLayout, context)
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.LABOUR_ROUTE_PAGE,
                null
            )
        }

        with(view as _LinearLayout) {
            verticalLayout {
                weightSum = 1F
                verticalLayout {
                    loadImageRoute(view)
                }.lparams(width = matchParent, height = dip(0), weight = 0.35F)
                verticalLayout {
                    verticalLayout {
                        loadImageAudio()
                    }.lparams(width = matchParent, height = wrapContent) {
                        topMargin = dip(sizeSpaceButtonAndImage.toFloat())
                    }

                    verticalLayout {
                        showMessage(FIRST_MESSAGE, R.font.proxima_nova_bold)
                    }
                    verticalLayout {
                        showMessageDescription(
                            secondMessage,
                            R.font.proxima_nova_light
                        )
                    }.lparams(width = wrapContent, height = wrapContent) {
                        gravity = Gravity.CENTER
                    }
                    relativeLayout {
                        val paddingTopAndBottom = getHeightElementInDp(SPACE_LINK_BUTTON)
                        gravity = Gravity.BOTTOM or Gravity.CENTER
                        verticalLayout {
                            buttonNext(view)
                            viewLastResult(
                                paddingTopAndBottom,
                                MARGIN_FOR_TOP_AND_BOTTOM
                            )
                        }.lparams(width = matchParent, height = wrapContent) {
                            alignParentBottom()
                        }
                    }.lparams(width = matchParent, height = wrapContent)
                }.lparams(width = matchParent, height = dip(0), weight = 0.65F)
            }.lparams(width = matchParent, height = matchParent) {
                topMargin = dip(marginMainLayout)
                leftMargin = dip(marginMainLayout)
                rightMargin = dip(marginMainLayout)
            }
        }
        loadTitleRoute(isLabourRoute)
    }

    private fun @AnkoViewDslMarker _LinearLayout.viewLastResult(
        paddingTopAndBottom: Double,
        marginTopAndBottom: Double
    ) {
        verticalLayout {
            linkLastResult()
            isFocusable = true
            isClickable = true
            if (!isThereLastResultForRoute) {
                visibility = GONE
            }
            setOnClickListener { view ->
                val bundle = Bundle()
                bundle.putString(Constants.IS_LABOUR_ROUTE, typeRoute)
                view.findNavController()
                    .navigate(R.id.routeResultsFragment, bundle, navOptions, null)

                logAnalyticsCustomContentTypeWithId(ContentType.LINK, FirebaseEvent.LINK)
            }
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.custom_ripple)
            topPadding =
                dip((paddingTopAndBottom).toFloat())
            bottomPadding =
                dip((paddingTopAndBottom).toFloat())

        }.lparams(width = matchParent, height = wrapContent) {
            topMargin =
                dip((paddingTopAndBottom * marginTopAndBottom).toFloat())
            bottomMargin =
                dip((paddingTopAndBottom * marginTopAndBottom).toFloat())
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

    private fun @AnkoViewDslMarker _LinearLayout.loadImageRoute(
        view: View
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                imageWelcome,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent)
    }

    private fun _LinearLayout.loadImageAudio() {
        imageView {
            val imageUrl = resources.getIdentifier(
                "icon_question_audio",
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(this)
                .load(imageUrl).fitCenter()
                .into(this)
            gravity = Gravity.CENTER
//            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            val contentQuestion = "$FIRST_MESSAGE \n $secondMessage"
            textToSpeech = UtilsTextToSpeech(context!!, null, null)
            onClick {
                textToSpeech?.speakOut(contentQuestion)
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
            }
        }.lparams(
            width = calculateHeightComponentsQuestion(),
            height = calculateHeightComponentsQuestion()
        )
    }

    private fun _LinearLayout.showMessage(message: String, idTypeFont: Int) {
        val topSizeSpaceBetweenTitleAndAudio = getHeightElementInDp(SPACE_TITLE_WITH_AUDIO)
        val bottomSizeSpaceBetweenTitleAndDescription =
            getHeightElementInDp(SPACE_DESCRIPTION_WITH_TITLE)
        textView {
            gravity = Gravity.CENTER
            text = message
            textSizeDimen = R.dimen.text_size_question_route
            typeface = ResourcesCompat.getFont(
                context.applicationContext, idTypeFont
            )
        }.lparams(width = wrapContent, height = wrapContent) {
            gravity = Gravity.CENTER
            topMargin = dip(topSizeSpaceBetweenTitleAndAudio.toFloat())
            bottomMargin = dip(bottomSizeSpaceBetweenTitleAndDescription.toFloat())
        }
    }

    private fun _LinearLayout.showMessageDescription(message: String, idTypeFont: Int) {
        textView {
            gravity = Gravity.CENTER
            text = message
            textSizeDimen = R.dimen.text_size_question_route
            typeface = ResourcesCompat.getFont(
                context.applicationContext, idTypeFont
            )
        }
    }

    private fun _LinearLayout.buttonNext(view: _LinearLayout) {
        val height = getHeightElementInDp(Constants.HEIGHT_OF_BUTTON)
        themedButton(theme = R.style.ButtonNext) {
            text = ROUTE_BUTTON_TEXT
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorBackgroundCategoryRoute)
            allCaps = false
            backgroundDrawable =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.drawable_multiple_option_next_button
                )
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_bold
            )
            onClick {
                nextButtonClick(view)
            }
        }.lparams(width = matchParent, height = dip(height.toFloat())) {
            if (!isThereLastResultForRoute) {
                bottomMargin = dip(
                    (getHeightElementInDp(SPACE_LINK_BUTTON)
                            * MARGIN_FOR_TOP_AND_BOTTOM).toFloat()
                )
            }
        }

    }

    private fun nextButtonClick(view: _LinearLayout) {
        val isLabourRoute = typeRoute == Constants.ROUTE_LABOUR
        routeViewModel.deleteAnswersOptionFromSharedPreferences(isLabourRoute)
        if (isLabourRoute) {
            val goToBundle = Bundle().apply {
                putInt(Constants.REMAINING_QUESTION, Constants.TEMPORAL_LOAD_PROGRESS_BAR)
                putInt(Constants.QUESTION_ADVANCE, Constants.COUNT_QUESTION_ADVANCE)
                putInt(
                    Constants.ROUTE_QUESTION_GO_TO,
                    Constants.START_QUESTION_ROUTE_LABOUR
                )
                putSerializable(
                    Constants.ROUTE_LABOUR,
                    routeViewModel.loadRouteFromSharedPreferences(Constants.LABOUR_ROUTE)
                )
            }
            view.findNavController()
                .navigate(R.id.singleSelectQuestionFragment, goToBundle, navOptions, null)
        } else {
            val goToBundle = Bundle().apply {
                putInt(Constants.REMAINING_QUESTION, Constants.TEMPORAL_LOAD_PROGRESS_BAR)
                putInt(Constants.QUESTION_ADVANCE, Constants.COUNT_QUESTION_ADVANCE)
                putInt(
                    Constants.ROUTE_QUESTION_GO_TO,
                    Constants.START_QUESTION_ROUTE_VIOLENCE
                )
                putSerializable(
                    Constants.ROUTE_VIOLENCE,
                    routeViewModel.loadRouteFromSharedPreferences(Constants.VIOLENCE_ROUTE)
                    //VIOLENCE_ROUTE
                )
            }
            view.findNavController()
                .navigate(
                    R.id.multipleSelectionQuestionFragment,
                    goToBundle,
                    navOptions,
                    null
                )
        }
        logAnalyticsCustomContentTypeWithId(ContentType.ROUTE, FirebaseEvent.ROUTE)
    }

    private fun _LinearLayout.linkLastResult() {

        textView {
            gravity = Gravity.CENTER
            text = getString(R.string.labour_route_result_link)
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext, R.font.proxima_nova_light
            )
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
        }
    }

    private fun loadMessagesRoute() {
        if (typeRoute == Constants.ROUTE_LABOUR) {
            secondMessage = getString(R.string.second_message_route_labour)
            imageWelcome = getString(R.string.image_name_route_labour)
        } else {
            secondMessage = getString(R.string.second_message_route_violence)
            imageWelcome = getString(R.string.image_name_route_violence)
        }
    }

    private fun calculateHeightComponentsQuestion(): Int {
        val cardHeightInDps =
            getHeightElementInDp(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE)
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun getHeightElementInDp(percentageComponent: Double) =
        (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
}
