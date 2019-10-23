package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.untha.R
import com.untha.model.models.CategoryViewModel
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import com.untha.view.adapters.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_category.*
import org.koin.android.viewmodel.ext.android.viewModel

class CategoryFragment : BaseFragment(),
    CategoryAdapter.OnItemClickListener, CategoryAdapter.OnItemLongClickListener {

    companion object {
        const val RIGHTS_CATEGORY = 2
        const val CALCULATOR_CATEGORY = 3
        const val ROUTES_CATEGORY = 1
    }

    private lateinit var categoryListAdapter: CategoryAdapter
    private val categoryViewModel: CategoryViewModel by viewModel()


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

            CALCULATOR_CATEGORY -> println("To be implemented")
            ROUTES_CATEGORY -> println("To be implemented")
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

    override fun onItemLongClick(itemView: View, text: String): Boolean {
        ToSpeech.speakOut(text, textToSpeech)
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.textToSpeech = TextToSpeech(context, this)
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(it, "Category Page", null)
        }
        setMarginsToRecyclerView()
        categoryViewModel.findMainCategories().observe(this, Observer { queryingCategories ->
            val categories = categoryViewModel.getCategories(queryingCategories)
            populateCategoryList(categories)
        })
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

    var onSpanSizeLookup: GridLayoutManager.SpanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (categoryListAdapter.getItemViewType(position) == Constants.MAIN_VIEW) {
                    Constants.SPAN_THREE_COLUMNS
                } else {
                    Constants.SPAN_ONE_COLUMN
                }
            }
        }
}
