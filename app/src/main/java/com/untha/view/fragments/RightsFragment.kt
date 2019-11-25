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
import com.untha.utils.PixelConverter
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.RightsAdapter
import com.untha.viewmodels.RightsViewModel
import kotlinx.android.synthetic.main.layout_rights.categoryRecyclerView
import me.linshen.retrofit2.adapter.ApiErrorResponse
import me.linshen.retrofit2.adapter.ApiSuccessResponse
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel


class RightsFragment : BaseFragment(),
    RightsAdapter.OnItemClickListener {

    private val viewModel: RightsViewModel by viewModel()
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity

        val rigthsView = inflater.inflate(R.layout.layout_rights, container, false)
        addShareButtonToView(rigthsView, inflater, container)

        return rigthsView
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
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_RIGHTS,
            enableCustomBar = true,
            needsBackButton = true,
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
        }
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

    private fun addShareButtonToView(
        rigthsView: View,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val shareButtonView = inflater.inflate(R.layout.share_button, container, false)
        val rigthsRelativeLayout: RelativeLayout = rigthsView.findViewById(R.id.rl_rights)
        val shareButtonRelativeLayout: RelativeLayout =
            shareButtonView.findViewById(R.id.rl_share_button)
        val shareButton: FloatingActionButton =
            shareButtonRelativeLayout.findViewById(R.id.share_button)

        shareButton.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_BUTTON_MESSAGE)
            sendIntent.type = "text/plain"
            context?.startActivity(sendIntent)
        }
        rigthsRelativeLayout.addView(shareButtonRelativeLayout)
    }
}
