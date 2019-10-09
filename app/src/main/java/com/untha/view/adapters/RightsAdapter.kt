package com.untha.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import kotlinx.android.synthetic.main.layout_rights_item.view.*


class RightsAdapter(
    private val items: List<Category>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(category: Category, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_rights_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position], clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category, listener: OnItemClickListener) =
            with(itemView) {
                setLayoutParams()
                categoryTitle.text = category.title.toUpperCase()
                loadImage(category)
                itemView.setOnClickListener {
                    listener.onItemClick(category, itemView)
                }
            }

        private fun View.loadImage(category: Category) {
            val heightScreen = ((PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_SMALL_RIGHTS)
            val imageView = findViewById<ImageView>(R.id.imageRightButton)
            imageView.setPadding((heightScreen / Constants.PERCENTAGE_PADDING_IMAGE_RIGHTS).toInt())
            val idImage = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(itemView)
                .load(idImage)
                .into(itemView.imageRightButton)
        }

        private fun View.setLayoutParams() {
            val leftMarginInDps =
                PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_WIDTH_PERCENTAGE
            val marginLeft = PixelConverter.toPixels(leftMarginInDps, context)
            val heightScreen = ((PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_SMALL_RIGHTS)
            val width = PixelConverter.toPixels(heightScreen, context)
            val topMarginInDps = (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_SMALL_TOP_PERCENTAGE
            val marginTop = PixelConverter.toPixels(topMarginInDps, context)

            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width)
            params.setMargins(marginLeft, marginTop, 0, 0)
            rightsContainer.layoutParams = params
        }
    }
}
