package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.view.adapters.RightsAdapter
import com.untha.viewmodels.RightsViewModel
import kotlinx.android.synthetic.main.layout_rights.categoryRecyclerView
import org.koin.android.viewmodel.ext.android.viewModel


class RightsFragment : Fragment(),
    RightsAdapter.OnItemClickListener{


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
        setMarginsToRecyclerView()
        // Start observing people list
        viewModel.getRightsCategoryModels().observe(this, Observer { queryingCategories ->
            val categories = viewModel.getRightCategories(queryingCategories)
            populateCategoryList(categories)
        })
    }

    /**
     * Populates categoryRecyclerView with all people info
     */
    private fun populateCategoryList(categoryList: List<Category>) {
        val layoutManager = GridLayoutManager(context, Constants.SPAN_TWO_COLUMNS)
        categoryRecyclerView.layoutManager = layoutManager
        categoryRecyclerView.adapter = RightsAdapter(categoryList, this)
    }


    /**
     * Navigates to people details on item click
     */
    override fun onItemClick(category: Category, itemView: View) {
//        val peopleBundle = Bundle().apply {
//            putInt(getString(R.string.people_id), people.id)
//        }
//        view?.findNavController()
//            ?.navigate(R.id.action_peoplesListFragment_to_peopleDetailsFragment, peopleBundle)

    }



    private fun setMarginsToRecyclerView() {

        val topFormula =
            PixelConverter.getScreenDpHeight(context) * Constants.MARGIN_TOP_PERCENTAGE
        context?.let { context ->
            val pixelBottomMargin = PixelConverter.toPixels(topFormula, context)


            val param = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            param.setMargins(0, pixelBottomMargin, pixelBottomMargin, pixelBottomMargin)
            categoryRecyclerView.layoutParams = param
        }
    }
}
