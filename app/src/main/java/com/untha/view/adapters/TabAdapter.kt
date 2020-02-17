package com.untha.view.adapters

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments = arrayListOf<Fragment>()
    private val fragmentTitles = arrayListOf<String>()

    override fun getItem(position: Int): Fragment {
        return fragments.get(position);
    }

    override fun getCount(): Int {
        return fragments.size
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles.get(position)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }
}
