package com.untha.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.CategoryAdapter
import com.untha.viewmodels.AboutUsViewModel
import com.untha.viewmodels.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_category.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel

class CategoryFragment : BaseFragment(),
    CategoryAdapter.OnItemClickListener, CategoryAdapter.OnItemLongClickListener {

    companion object {
        const val RIGHTS_CATEGORY = 2
        const val CALCULATOR_CATEGORY = 5
        const val ROUTES_CATEGORY = 1
    }

    private lateinit var routes: ArrayList<Category>
    private lateinit var calculators: ArrayList<Category>

    private lateinit var categoryListAdapter: CategoryAdapter
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val viewModel: AboutUsViewModel by viewModel()
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textToSpeech = UtilsTextToSpeech(context!!, null, null)
        mainActivity = this.activity as MainActivity
        val categoriesView = inflater.inflate(R.layout.fragment_category, container, false)
        addShareButtonToView(categoriesView, inflater, container)
        return categoriesView
    }

    override fun isLastScreen(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!viewModel.loadAboutUsFromSharedPreferences()) {
            view.findNavController()
                .navigate(
                    R.id.trhAboutInstructions,
                    null,
                    navOptionsToBackNavigation,
                    null
                )
        }
        activity?.let {
            firebaseAnalytics.setCurrentScreen(it, Constants.CATEGORY_PAGE, null)
        }
        setMarginsToRecyclerView()
        categoryViewModel.findMainCategories().observe(this, Observer { queryingCategories ->
            categoryViewModel.getCategories(queryingCategories)
            getCategoryRoutes()
            getCategoryCalculators()
            populateCategoryList(categoryViewModel.categories)
        })
        (activity as MainActivity).customActionBar(
            Constants.HOME_PAGE,
            enableCustomBar = true,
            needsBackButton = false,
            enableHelp = true,
            backMethod = null
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

    override fun onItemClick(category: Category, categories: ArrayList<Category>, itemView: View) {
        logAnalyticsSelectContentWithId(
            category.title, ContentType.CATEGORY
        )
        when (category.id) {
            RIGHTS_CATEGORY -> itemView.findNavController().navigate(
                R.id.rightsFragment,
                null,
                navOptions,
                null
            )

            CALCULATOR_CATEGORY -> {
                val categoriesRoutes = Bundle().apply {
                    putSerializable(
                        Constants.CATEGORIES_CALCULATORS,
                        calculators
                    )
                }
                itemView.findNavController().navigate(
                    R.id.calculatorsFragment,
                    categoriesRoutes,
                    navOptions,
                    null
                )
            }

            ROUTES_CATEGORY -> {

                activity?.let {
                    firebaseAnalytics.setCurrentScreen(it, Constants.CLICK_ROUTE_START_TITLE, null)
                }

                val categoriesRoutes = Bundle().apply {
                    putSerializable(
                        Constants.CATEGORIES_ROUTES,
                        routes
                    )
                }
                itemView.findNavController().navigate(
                    R.id.mainRouteFragment,
                    categoriesRoutes,
                    navOptions,
                    null
                )
            }

            else -> {
                val categoryBundle = Bundle().apply {
                    putSerializable(Constants.CATEGORY_PARAMETER, category)
                    putSerializable(Constants.CATEGORIES, categories)
                }
                itemView.findNavController()
                    .navigate(R.id.genericInfoFragment, categoryBundle, navOptions, null)
            }
        }
    }

    private fun getCategoryRoutes() {
        routes = categoryViewModel.getCategoryRoutes()
        categoryViewModel.saveCategoryRoutesInSharedPreferences(routes)
    }

    private fun getCategoryCalculators() {
        calculators = categoryViewModel.getCategoryCalculators()
        categoryViewModel.saveCategoryCalculatorsInSharedPreferences(calculators)
    }
    override fun onItemLongClick(itemView: View, text: String): Boolean {
        textToSpeech?.speakOut(text)
        logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
        return true
    }

    private fun setMarginsToRecyclerView() {
        val topFormula =
            (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE
        context?.let { context ->
            val pixelBottomMargin = PixelConverter.toPixels(topFormula, context)

            val param = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            param.setMargins(0, 0, pixelBottomMargin, pixelBottomMargin)
            categoryRecyclerView.layoutParams = param
        }
    }

    private fun populateCategoryList(categoryList: List<Category>) {
        val gridLayoutManager = GridLayoutManager(context, Constants.SPAN_THREE_COLUMNS)
        gridLayoutManager.spanSizeLookup = onSpanSizeLookup
        categoryRecyclerView.layoutManager = gridLayoutManager
        categoryListAdapter = CategoryAdapter(categoryList, this, this)
        categoryRecyclerView.adapter = categoryListAdapter
    }

    private var onSpanSizeLookup: GridLayoutManager.SpanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (categoryListAdapter.getItemViewType(position) == Constants.MAIN_VIEW) {
                    Constants.SPAN_THREE_COLUMNS
                } else {
                    Constants.SPAN_ONE_COLUMN
                }
            }
        }

    private fun addShareButtonToView(
        categoriesView: View,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val shareButtonView = inflater.inflate(R.layout.share_button, container, false)
        val categoriesRelativeLayout: RelativeLayout =
            categoriesView.findViewById(R.id.rl_categories)
        val shareButtonRelativeLayout: RelativeLayout =
            shareButtonView.findViewById(R.id.rl_share_button)
        val shareButton: FloatingActionButton =
            shareButtonRelativeLayout.findViewById(R.id.share_button)
        shareButton.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_app))
            sendIntent.type = "text/plain"
            context?.startActivity(sendIntent)
        }

        categoriesRelativeLayout.addView(shareButtonRelativeLayout)
    }
}
