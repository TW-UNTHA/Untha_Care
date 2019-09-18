package com.untha_care.viewmodels.listCategoryDerechos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.untha_care.R
import com.untha_care.model.models.Category
import kotlinx.android.synthetic.main.category_derechos_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class CategoryListFragment : Fragment(),
    CategoryListAdapter.OnItemClickListener,
    SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {

    override fun onClose(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val viewModel: CategoryListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.category_derechos_fragment, container, false)
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
        viewModel.getCategoryList().observe(this, Observer<List<Category>> { categories ->
            categories?.let {
                populateCategoryList(categories)
            }
        })
    }

    /**
     * Populates categoryRecyclerView with all people info
     */
    private fun populateCategoryList(categoryList: List<Category>) {
        categoryRecyclerView.adapter  = CategoryListAdapter(categoryList, this)
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

}

