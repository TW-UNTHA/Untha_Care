package com.untha.view.adapters

import android.content.Context
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
import kotlinx.android.synthetic.main.layout_category_main_item.view.*
import kotlinx.android.synthetic.main.layout_category_main_item.view.textViewCategoryTitle
import kotlinx.android.synthetic.main.layout_category_small_item.view.*

class CategoryAdapter(
    private val items: List<Category>,
    private val clickListener: OnItemClickListener,
    private val onLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(category: Category, categories: ArrayList<Category>, itemView: View)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(itemView: View, text: String): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == Constants.MAIN_VIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_category_main_item, parent, false)
            return CategoryMainViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_category_small_item, parent, false)

        return CategorySmallViewHolder(view)
    }


    override fun getItemCount(): Int {
        return items.filter { !it.isRoute }.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!items[position].isRoute) {
            if (position == Constants.MAIN_VIEW) {
                (holder as CategoryMainViewHolder).bind(
                    items[position],
                    clickListener,
                    onLongClickListener
                )
            } else {
                (holder as CategorySmallViewHolder).bind(
                    items[position],
                    clickListener,
                    onLongClickListener
                )
            }
        }
    }

    inner class CategoryMainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            category: Category,
            clickListener: OnItemClickListener,
            longClickListener: OnItemLongClickListener
        ) = with(itemView) {
            textViewCategoryTitle.text = category.subtitle
            setLayoutParams2(
                Constants.PERCENTAGE_MAIN_HEIGHT_LAYOUT,
                Constants.MARGIN_TOP_PERCENTAGE,
                rl_main_item, context
            )
            loadImage(category)
            setOnClickListener { view ->
                clickListener.onItemClick(category, items as ArrayList<Category>, itemView)
            }
            setOnLongClickListener {
                val prefix = "Ir a, "
                val text = prefix + category.title
                longClickListener.onItemLongClick(itemView, text)
            }
        }

        private fun View.loadImage(
            category: Category
        ) {
            val imageView = findViewById<ImageView>(R.id.imageView)
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(itemView)
                .load(imageUrl).fitCenter()
                .into(imageView)
        }
    }


    inner class CategorySmallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            category: Category,
            clickListener: OnItemClickListener,
            longClickListener: OnItemLongClickListener
        ) {
            with(itemView) {


                textViewCategoryTitle.text = category.title
                setLayoutParams2(
                    Constants.PERCENTAGE_SMALL_HEIGHT_LAYOUT,
                    Constants.MARGIN_SMALL_TOP_PERCENTAGE,
                    rl_small_item, context
                )
                loadSmallImage(category)

                val prefix = "Ir a , "
                val text = prefix + category.title
                setOnClickListener {
                    clickListener.onItemClick(category, items as ArrayList<Category>, itemView)
                }
                setOnLongClickListener {
                    longClickListener.onItemLongClick(itemView, text)
                }

            }
        }

        private fun View.loadSmallImage(
            category: Category
        ) {
            val imageView = findViewById<ImageView>(R.id.imageView)
            val cardHeightInDps =
                (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_SMALL_HEIGHT_LAYOUT
            imageView.setPadding((cardHeightInDps / Constants.PERCENTAGE_PADDING_SMALL_IMAGE_VIEW).toInt())

            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )

            Glide.with(itemView)
                .load(imageUrl)
                .into(imageView)
        }

    }


    private fun setLayoutParams2(
        percentageHeightLayout: Double,
        marginTopPercentage: Double,
        relativeLayout: RelativeLayout, context: Context
    ) {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * percentageHeightLayout
        val width = PixelConverter.toPixels(cardHeightInDps, context)
        val topMarginDps = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * marginTopPercentage
        val marginTop = PixelConverter.toPixels(topMarginDps, context)
        val cardWidthInDps =
            PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_WIDTH_PERCENTAGE
        val marginLeft = PixelConverter.toPixels(cardWidthInDps, context)


        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width)
        params.setMargins(marginLeft, marginTop, 0, 0)
        relativeLayout.layoutParams = params
    }

    override fun getItemViewType(position: Int): Int {
        if (position == Constants.MAIN_ITEM_RECYCLE_VIEW) {
            return Constants.MAIN_VIEW
        }
        return Constants.SMALL_VIEW
    }
}
