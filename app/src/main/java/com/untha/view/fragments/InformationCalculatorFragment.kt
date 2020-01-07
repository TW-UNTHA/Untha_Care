package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Section
import com.untha.utils.Constants
import com.untha.utils.UtilsTextToSpeech
import com.untha.viewmodels.InformationCalculatorViewModel
import kotlinx.android.synthetic.main.fragment_information_calculator.*
import kotlinx.android.synthetic.main.fragment_information_calculator.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class InformationCalculatorFragment : BaseFragment(), View.OnClickListener {

    private lateinit var category: Category
    private lateinit var categories: ArrayList<Category>
    private lateinit var playAudio: ImageButton
    private lateinit var section: Section
    private val calculatorViewModel : InformationCalculatorViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        category = bundle?.get(Constants.CATEGORY_PARAMETER) as Category
        categories = bundle?.get(Constants.CATEGORIES) as ArrayList<Category>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_information_calculator, container, false)

        instanceUI(view)

        return view
    }

    private fun instanceUI(view: View) {
        val title = view.title
        val description = view.description
        val rightsButton = view.rightsButton
        val contractsButton = view.contractsButton

        rightsButton.setOnClickListener(this)
        contractsButton.setOnClickListener(this)
        playAudio = view.playAudio
        Glide.with(view).load(
           R.drawable.icon_question_audio
        ).into(
            playAudio
        )
        textToSpeech = UtilsTextToSpeech(context!!, null, null)
        playAudio.setOnClickListener(this)
        section = category.information!![0].sections!![0]
        title.text = section.title
        description.text = section.steps!![0].description
    }

    override fun onClick(clickedView: View?) {
        when(clickedView!!.id) {
            playAudio.id -> {
                textToSpeech!!.speakOut(section.steps!![0].description)
            }
            rightsButton.id -> {
                view!!.findNavController()
                    .navigate(
                        R.id.rightsFragment,
                        null,
                        navOptionsToBackNavigation,
                        null
                    )
            }

            contractsButton.id -> {
                val categoryBundle = Bundle().apply {
                    putSerializable(Constants.CATEGORY_PARAMETER,
                        calculatorViewModel.getCategoryById(Constants.ID_CONTRACTS_CATEGORY, categories))
                    putSerializable(Constants.CATEGORIES, categories)
                }
                view!!.findNavController()
                    .navigate(
                        R.id.genericInfoFragment,
                        categoryBundle,
                        navOptionsToBackNavigation,
                        null
                    )
            }
        }

    }
}
