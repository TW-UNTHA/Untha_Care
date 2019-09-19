package com.untha_care.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.untha_care.R
import com.untha_care.model.models.Category
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
            categoryTitle.text = category.title
            categoryDescription.text = category.description
//            buttonContact.setOnClickListener {
//                // Dial contact number
//                val dialIntent = android.content.Intent(android.content.Intent.ACTION_DIAL)
//                dialIntent.data = android.net.Uri.parse("tel:${people.contact}")
//                itemView.context.startActivity(dialIntent)
//            }

            // RecyclerView on item click
//            setOnClickListener {
//                listener.onItemClick(people, it)
//            }
        }

    }

}
