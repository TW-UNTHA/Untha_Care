package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.SlidePagerAdapter
import com.untha.viewmodels.AboutUsViewModel
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.koin.android.viewmodel.ext.android.viewModel


class AboutUsFragment : BaseFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var mainActivity: MainActivity
    private val viewModel: AboutUsViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this.activity as MainActivity
        mainActivity.customActionBar(
            Constants.HOME_PAGE,
            enableCustomBar = true,
            needsBackButton = false,
            enableHelp = false,
            backMethod = null
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goBackHome()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.saveAboutUsPreferences(true)
        mainActivity = this.activity as MainActivity
        textToSpeech = UtilsTextToSpeech(context!!, null, null)
        val test = inflater.inflate(R.layout.activity_screen_slide, container, false)
        viewPager = test.findViewById(R.id.pager)
        val pagerAdapter =
            SlidePagerAdapter((activity as MainActivity).supportFragmentManager, textToSpeech!!)
        viewPager.adapter = pagerAdapter
        val indicator = test.findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        indicator.setViewPager(viewPager)
        return test
    }

    private fun goBackHome() {
        val layoutActionBar = (activity as MainActivity).supportActionBar?.customView
        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        close.onClick {
            view?.findNavController()
                ?.navigate(
                    R.id.categoryFragment,
                    null,
                    navOptionsToBackNavigation,
                    null
                )
        }

    }

    override fun onStop() {
        textToSpeech?.stop()
        super.onStop()
    }

    override fun onDestroy() {
        textToSpeech?.destroy()
        super.onDestroy()
    }
}

