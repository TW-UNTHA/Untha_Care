package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.untha.R
import com.untha.model.models.Category
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.RightsAdapter
import com.untha.view.util.HorizontalSpaceItemDecoration
import com.untha.viewmodels.RightsViewModel
import kotlinx.android.synthetic.main.layout_rights.*
import org.koin.android.viewmodel.ext.android.viewModel


class RightsFragment : Fragment(),
    RightsAdapter.OnItemClickListener,
    SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {

    private val HORIZONTALITEMSPACE = 20
    private lateinit var mainActivity: MainActivity


    override fun onClose(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val viewModel: RightsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        return inflater.inflate(R.layout.layout_rights, container, false)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start observing people list
        viewModel.getRightsList().observe(this, Observer<List<Category>> { categories ->
            categories?.let {
                println("categories"+categories)
                populateCategoryList(categories)
            }
        })
    }


    /**
     * Populates categoryRecyclerView with all people info
     */
    private fun populateCategoryList(categoryList: List<Category>) {
        val layout_manager = GridLayoutManager(activity, 2)
        categoryRecyclerView.layoutManager = layout_manager

        categoryRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(HORIZONTALITEMSPACE))

        categoryRecyclerView.adapter  = RightsAdapter(categoryList, this)
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

    override fun onResume() {
        super.onResume()
        mainActivity.supportActionBar?.title = "MIS DERECHOS"
//        mainActivity.actionBar.setHomeAsUpIndicator()
    }



}