package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.view.activities.CategoryListAdapter
import com.untha.viewmodels.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_category.*
import org.koin.android.viewmodel.ext.android.viewModel

class CategoryFragment : BaseFragment(),
    CategoryListAdapter.OnItemClickListener {
    private lateinit var categoryListAdapter: CategoryListAdapter
    private val categoryViewModel: CategoryViewModel by viewModel()


    override fun onItemClick(category: Category, itemView: View) {
        println("default just by gradle.detekt")
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
        categoryListAdapter = CategoryListAdapter(categoryList, this, context)
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
