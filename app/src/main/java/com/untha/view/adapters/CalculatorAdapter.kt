package com.untha.view.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import kotlinx.android.synthetic.main.layout_calculator_item.view.*

class CalculatorAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_calculator_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String) =
            with(itemView) {
                setLayoutParams()
                calculatorTitle.text = item.toUpperCase()
                loadImage()
                itemView.setOnClickListener {
                    println("calculatoAdapter")
                }
            }

        private fun View.loadImage() {
            val screenWidth = PixelConverter.getScreenDpWidth(context)
            val cardHeightInDps =
                if (screenWidth <= Constants.WIDTH_TWO_INCHES_DEVICES)
                    Constants.PADDING_FOR_IMAGES_IN_TWO_INCHES_DEVICES else
                    (screenWidth * Constants.SIZE_MARGIN_IMAGE).toInt()
            itemView.imageCalculatorButton.setPadding(cardHeightInDps)
            val idImage = resources.getIdentifier(
                "home_beneficios",
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(itemView)
                .load(idImage)
                .into(itemView.imageCalculatorButton)
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
            calculatorContainer.layoutParams = params
        }
    }
}
