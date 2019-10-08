package com.untha.view.activities

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import kotlinx.android.synthetic.main.layout_category_main_item.view.*
import kotlinx.android.synthetic.main.layout_category_main_item.view.textViewCategoryTitle
import kotlinx.android.synthetic.main.layout_category_small_item.view.*

class CategoryListAdapter(
    private val items: List<Category>,
    private val clickListener: OnItemClickListener,
    private val textToSpeech: TextToSpeech?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(category: Category, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == Constants.MAIN_VIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.untha.R.layout.layout_category_main_item, parent, false)
            return CategoryMainViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(com.untha.R.layout.layout_category_small_item, parent, false)

        return CategorySmallViewHolder(view)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == Constants.MAIN_VIEW) {
            (holder as CategoryMainViewHolder).bind(
                items[position],
                clickListener,
                textToSpeech
            )
        } else {
            (holder as CategorySmallViewHolder).bind(items[position], clickListener, textToSpeech)
        }
    }

    class CategoryMainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(
            category: Category,
            listener: OnItemClickListener,
            textToSpeech: TextToSpeech?
        ) = with(itemView) {
            val heightFormula = (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_MAIN_LAYOUT
            val width = PixelConverter.toPixels(heightFormula, context)
            val topFormula = (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE
            val marginTop = PixelConverter.toPixels(topFormula, context)
            val widthFormula =
                (PixelConverter.getScreenDpWidth(context)) * Constants.MARGIN_WIDTH_PERCENTAGE
            val marginLeft = PixelConverter.toPixels(widthFormula, context)

            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width)
            params.setMargins(marginLeft, marginTop, 0, 0)

            rl_main_item.layoutParams = params
            textViewCategoryTitle.text = category.subtitle

            val imageView = findViewById<ImageView>(com.untha.R.id.imageView)
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(itemView)
                .load(imageUrl).fitCenter()
                .into(imageView)
            val prefix = "Ir a , "
            val title = textViewCategoryTitle!!.text.toString()
            val buttonLayout = findViewById<RelativeLayout>(com.untha.R.id.rl_main_item)
            buttonLayout.setOnLongClickListener { ToSpeech.speakOut(prefix + title, textToSpeech) }

            setOnClickListener {
                listener.onItemClick(category, it)
            }
        }
    }


    class CategorySmallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(
            category: Category,
            listener: OnItemClickListener,
            textToSpeech: TextToSpeech?
        ) {
            with(itemView) {
                val heightFormula =
                    (PixelConverter.getScreenDpHeight(context) -
                            Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_SMALL_LAYOUT
                val width = PixelConverter.toPixels(heightFormula, context)

                val widthFormula =
                    PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_WIDTH_PERCENTAGE
                val marginLeft = PixelConverter.toPixels(widthFormula, context)

                val topFormula = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_SMALL_TOP_PERCENTAGE
                val marginTop = PixelConverter.toPixels(topFormula, context)

                val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width)
                params.setMargins(marginLeft, marginTop, 0, 0)
                rl_small_item.layoutParams = params

                textViewCategoryTitle.text = category.title
                val imageView = findViewById<ImageView>(com.untha.R.id.imageView)
                imageView.setPadding((heightFormula / Constants.PERCENTAGE_PADDING_SMALL_IMAGE_VIEW).toInt())

                val imageUrl = resources.getIdentifier(
                    category.image,
                    "drawable",
                    context.applicationInfo.packageName
                )

                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)

                val prefix = "Ir a , "

                val title = textViewCategoryTitle!!.text.toString()
                val buttonLayout = findViewById<RelativeLayout>(com.untha.R.id.rl_small_item)
                buttonLayout.setOnLongClickListener {
                    ToSpeech.speakOut(
                        prefix + title,
                        textToSpeech
                    )
                }

                setOnClickListener {
                    listener.onItemClick(category, it)
                }

                itemView.setOnClickListener {
                    if (category.id == 2) {
                        itemView.findNavController().navigate(R.id.rightsFragment)
                    } else {

                        val categoryBundle = Bundle().apply {
                            putSerializable("category", category)
                        }

                        itemView?.findNavController()
                            ?.navigate(R.id.genericInfoFragment, categoryBundle)
                    }
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == Constants.PRIMER_ELEMENTO_MAIN_ITEM_RECYCLE_VIEW) {
            return Constants.MAIN_VIEW
        }
        return Constants.SMALL_VIEW
    }
}
