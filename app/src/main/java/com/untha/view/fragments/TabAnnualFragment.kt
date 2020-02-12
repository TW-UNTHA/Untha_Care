package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untha.view.activities.MainActivity

class TabAnnual : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.untha.R.layout.fragment_item_result_annual,
            container,
            false
        )
    }
}