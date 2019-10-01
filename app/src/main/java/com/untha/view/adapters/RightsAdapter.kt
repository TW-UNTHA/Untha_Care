package com.untha.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import kotlinx.android.synthetic.main.layout_rights_item.view.*


class RightsAdapter(
    val items: List<Category>,
    val clickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Notifies click on an item with attached view
     */
    interface OnItemClickListener {
        fun onItemClick(category: Category, itemView: View){
         print("easklhdalskhdakjshdiouyerwe90679806798789")
        }

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

        fun bind(category: Category, @Suppress("UNUSED_PARAMETER")listener : OnItemClickListener) = with(itemView) {


            categoryTitle.text = category.title.toUpperCase()

            val heightScreen = ((PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_SMALL_RIGHTS)

            val width = PixelConverter.toPixels(heightScreen, context)

            val widthFormula = PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_WIDTH_PERCENTAGE


            val marginLeft = PixelConverter.toPixels(widthFormula, context)

            val topFormula = (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_SMALL_TOP_PERCENTAGE
            val marginTop = PixelConverter.toPixels(topFormula, context)

            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width)
            params.setMargins(marginLeft, marginTop, 0, 0)
            rightsContainer.layoutParams = params

            val imageView = findViewById<ImageView>(R.id.imageRightButton)
            imageView.setPadding( Constants.RIGHTS_PADDING,
                Constants.RIGHTS_PADDING * Constants.TOP_PADDING_MULTIPLIER,
                Constants.RIGHTS_PADDING,
                Constants.RIGHTS_PADDING)

            val idImage = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(itemView)
                .load(idImage)
                .into(itemView.imageRightButton)
        }

    }
}



