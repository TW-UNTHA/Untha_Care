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
import com.untha.view.adapters.RightsAdapter
import com.untha.viewmodels.RightsViewModel
import kotlinx.android.synthetic.main.layout_rights.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel


class RightsFragment : BaseFragment(),
    RightsAdapter.OnItemClickListener, RightsAdapter.OnItemLongClickListener {

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
        textToSpeech = UtilsTextToSpeech(context!!, null, null)
        mainActivity = this.activity as MainActivity
        val rightsView = inflater.inflate(R.layout.layout_rights, container, false)
        addShareButtonToView(rightsView, inflater, container)
        return rightsView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(it, Constants.RIGHTS_PAGE, null)
        }
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
            logAnalyticsCustomContentTypeWithId(ContentType.HELP, FirebaseEvent.HELP)
        }
    }

    private fun populateCategoryList(categoryList: List<Category>) {
        val layoutManager = GridLayoutManager(context, Constants.SPAN_TWO_COLUMNS)
        categoryRecyclerView.layoutManager = layoutManager
        categoryRecyclerView.adapter =
            RightsAdapter(categoryList as ArrayList<Category>, this, this)
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

    private fun addShareButtonToView(
        rightsView: View,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val shareButtonView = inflater.inflate(R.layout.share_button, container, false)
        val rightsRelativeLayout: RelativeLayout = rightsView.findViewById(R.id.rl_rights)
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
        rightsRelativeLayout.addView(shareButtonRelativeLayout)
    }

    override fun onItemLongClick(itemView: View, text: String): Boolean {
        textToSpeech?.speakOut(text)
        logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
        return true
    }
}
