package com.untha.view.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class HorizontalSpaceItemDecoration(private val horizontalSpaceHeight: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = horizontalSpaceHeight
        outRect.bottom = horizontalSpaceHeight
    }
}