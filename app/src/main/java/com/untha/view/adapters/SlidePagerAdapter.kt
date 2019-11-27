package com.untha.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.fragments.SlideAboutUsFragment

private const val NUM_PAGES = 4

open class SlidePagerAdapter(
    fragmentManager: FragmentManager,
    private val textToSpeech: UtilsTextToSpeech
) :
    FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int = NUM_PAGES
    override fun getItem(position: Int): Fragment {
        return SlideAboutUsFragment(textToSpeech).newInstance(position)
    }
}
