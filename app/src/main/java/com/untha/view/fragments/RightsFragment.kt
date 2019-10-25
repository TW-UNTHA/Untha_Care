package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.view.adapters.RightsAdapter
import com.untha.viewmodels.RightsViewModel
import kotlinx.android.synthetic.main.layout_rights.categoryRecyclerView
import me.linshen.retrofit2.adapter.ApiErrorResponse
import me.linshen.retrofit2.adapter.ApiSuccessResponse
import org.koin.android.viewmodel.ext.android.viewModel


class RightsFragment : BaseFragment(),
    RightsAdapter.OnItemClickListener {

    private val viewModel: RightsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.layout_rights, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(it, "Rights Page", null)
        }
        config()
        setMarginsToRecyclerView()
        viewModel.getRightsCategoryModels().observe(this, Observer { queryingCategories ->
            val categories = viewModel.getRightCategories(queryingCategories)
            populateCategoryList(categories)
        })
    }

    private fun populateCategoryList(categoryList: List<Category>) {
        val layoutManager = GridLayoutManager(context, Constants.SPAN_TWO_COLUMNS)
        categoryRecyclerView.layoutManager = layoutManager
        categoryRecyclerView.adapter = RightsAdapter(categoryList as ArrayList<Category>, this)
    }


    private fun setMarginsToRecyclerView() {
        val marginInDps =
            PixelConverter.getScreenDpHeight(context) * Constants.MARGIN_TOP_PERCENTAGE
        context?.let { context ->
            val pixelBottomMargin = PixelConverter.toPixels(marginInDps, context)

            val param = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            param.setMargins(0, pixelBottomMargin, pixelBottomMargin, pixelBottomMargin)
            categoryRecyclerView.layoutParams = param
        }
    }

    override fun onItemClick(
        category: Category,
        categories: List<Category>?,
        itemView: View
    ) {
        val categoryBundle = Bundle().apply {
            putSerializable(Constants.CATEGORY_PARAMETER, category)
            putSerializable(Constants.CATEGORIES, categories as ArrayList<Category>?)
        }
        itemView.findNavController()
            .navigate(R.id.genericInfoFragment, categoryBundle, navOptions, null)
    }

    fun config() {
        viewModel.getRoutes().observe(this, Observer {
            print("getRoutes!!!")
            when (it) {

                is ApiSuccessResponse -> {
                    print("!!!!!!!!!!!!!")
                    println(it.body)
                }
                is ApiErrorResponse -> {
                    println("Errror!!!!!!!!!!!!! $it.errorMessage")
                }
            }
        })
    }

}
