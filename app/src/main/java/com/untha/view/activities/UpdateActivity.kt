package com.untha.view.activities

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.UpdateWrapper
import com.untha.viewmodels.UpdateViewModel
import kotlinx.android.synthetic.main.activity_update.*
import me.linshen.retrofit2.adapter.ApiErrorResponse
import me.linshen.retrofit2.adapter.ApiSuccessResponse
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber


class UpdateActivity : AppCompatActivity() {

    companion object {
        const val INTERNET_AVAILABLE = "IS_INTERNET_AVAILABLE"
    }

    val viewModel: UpdateViewModel by viewModel()
    private var isThereGooglePlayError = false
    private lateinit var updateValues: UpdateWrapper
    private var isInternetAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        setProgressBarColor()
        isThereGooglePlayError = viewModel.isThereGooglePlayError()
        isInternetAvailable = isRetrievingDataFromInternet() && !isThereGooglePlayError
        loadUpdateValues()
    }

    private fun isRetrievingDataFromInternet(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    private fun loadUpdateValues() {
        if (isInternetAvailable) {
            viewModel.retrieveUpdatedConfig().observe(this, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        buildUpdateValues(response)
                        if (this.getCurrentVersionCode() >= updateValues.version) {
                            navigateToMainScreen()
                        } else {
                            defineComponentVisibility()
                        }
                    }
                    is ApiErrorResponse -> {
                        Timber.e("Error! $response.errorMessage")
                        navigateToMainScreen()
                    }
                }
            })
        } else {
            navigateToMainScreen()
        }
    }

    private fun goToPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName&hl=es")
        startActivity(intent)
        finish()
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun defineComponentVisibility() {
        setDiscardButtonConfig()
        tvUpdateMessage.text = updateValues.messageUpdate
        loadImage()
        btnUpdate.setOnClickListener {
            goToPlayStore()
        }
        llProgressBarContainer.visibility = GONE
    }

    private fun setProgressBarColor() {
        setColorFilter(
            progressBar.indeterminateDrawable,
            ContextCompat.getColor(this, R.color.colorGenericTitle)
        )
    }

    private fun setDiscardButtonConfig() {
        if (updateValues.forceUpdate) {
            btnDiscard.isClickable = false
            btnDiscard.isFocusable = false
            btnDiscard.visibility = INVISIBLE
        } else {
            btnDiscard.isClickable = true
            btnDiscard.isFocusable = true
            btnDiscard.visibility = VISIBLE
        }
        btnDiscard.setOnClickListener {
            navigateToMainScreen()
        }
    }

    private fun loadImage() {
        Glide.with(this)
            .load(R.mipmap.ic_launcher)
            .into(ivIcon)
    }

    private fun buildUpdateValues(response: ApiSuccessResponse<UpdateWrapper>) {
        updateValues = UpdateWrapper(
            response.body.version,
            response.body.forceUpdate,
            response.body.messageUpdate
        )
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(INTERNET_AVAILABLE, isInternetAvailable)
        startActivity(intent)
        finish()
    }

    private fun getCurrentVersionCode(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode
        } else {
            packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
        }
    }
}
