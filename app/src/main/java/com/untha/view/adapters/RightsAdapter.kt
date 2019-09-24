package com.untha.view.adapters

import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.R.id.rightsContainer
import com.untha.model.models.Category
import kotlinx.android.synthetic.main.layout_rights_details.view.*

class RightsAdapter(
    private val items: List<Category>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Notifies click on an item with attached view
     */
    interface OnItemClickListener {
        fun onItemClick(category: Category, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_rights_details, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position], clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: Category, listener: OnItemClickListener) = with(itemView) {
            categoryTitle.text = category.title.toUpperCase()

            val idImage = resources.getIdentifier(category.image, "drawable", context.applicationInfo.packageName)
            Glide.with(itemView)
                .load(idImage)
                .into(itemView.imageRightButton)

            itemView.setOnClickListener {
                //Toast.makeText(context,"clicked on "+categoryTitle.text, Toast.LENGTH_SHORT).show()

                itemView.findNavController().navigate(R.id.mainFragment)

            }



        }

    }

}



