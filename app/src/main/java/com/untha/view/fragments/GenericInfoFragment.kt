package com.untha.view.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.utils.PixelConverter.toPixels
import com.untha.view.adapters.RightsAdapter
import kotlinx.serialization.json.Json
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.colorAttr
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textAppearance
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


class GenericInfoFragment : Fragment(),
    RightsAdapter.OnItemClickListener {

    companion object {
        const val topAndBottomPaddingMustBeCalculated = 24
        const val imageHeightMustBeCalculated = 131
        const val bottomMarginForDescriptionMustBeCalculated = 10
        const val bottomMarginForStepsMustBeCalculated = 16
        const val bottomMarginMustBeCalculated = 18
        const val leftAndRightPaddingMustBeCalculated = 13
    }

    private lateinit var category: Category
    private val categoryJson = "{\n" +
            "      \"id\": 6,\n" +
            "      \"image\": \"home_afiliciacion\",\n" +
            "      \"title\": \"VERIFICAR AFILIACIÓN\",\n" +
            "      \"information\": {\n" +
            "        \"description\": \"El empleador debe inscribirte en el Instituto " +
            "Ecuatoriano de Seguridad Social (IESS), desde el primer día de labores. " +
            "Dará aviso de tu entrada en los primeros 15 días. Avisará sobre tu salida, sueldos " +
            "y salarios, accidentes de trabajo y enfermedades que se produzcan en él. Y cumplirá " +
            "con las demás obligaciones legales sobre seguridad social.Para verificar si estás" +
            " afiliada" +
            " sigue estos pasos:\",\n" +
            "        \"sections\": [\n" +
            "          {\n" +
            "            \"id\": 1,\n" +
            "            \"title\": \"Obtén tu clave del IESS\",\n" +
            "            \"steps\": [\n" +
            "              {\n" +
            "                \"step_id\": 1,\n" +
            "                \"description\": \"Desde una computadora o celular con conexión al " +
            "internet, ingresa en el vínculo " +
            "<a href='https://www.iess.gob.ec/solicitudclave/pages/public/principal.jsf\'>Google</a></string>\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 2,\n" +
            "                \"description\": \"Ingresa tu número de cédula y sigue las " +
            "instrucciones hasta que aparezca el botón “Finalizar”\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 3,\n" +
            "                \"description\": \"Busca en tu correo un mail del IESS llamado " +
            "“Activación de clave” (antes de esto se te solicitará un correo elctrónico)." +
            " Selecciona el botón “Click para continuar” en el correo que te llegó\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 4,\n" +
            "                \"description\": \"Ingresa una nueva clave y repite la misma clave" +
            " para confirmar\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 2,\n" +
            "            \"title\": \"Obtén tu certificado de afiliación\",\n" +
            "            \"steps\": [\n" +
            "              {\n" +
            "                \"step_id\": 1,\n" +
            "                \"description\": \"Una vez que tengas tu clave del IESS, ingresa al " +
            "siguiente vínculo " +
            "https://www.iess.gob.ec/afiliado-web/pages/opcionesGenerales" +
            "/seleccionCertificadoDeAfiliacion.jsf\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 2,\n" +
            "                \"description\": \"Selecciona la opción “Certificado de Afiliación " +
            "al Seguro General”\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 3,\n" +
            "                \"description\": \"Ingresa tu número de cédula y después tu" +
            " clave\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 4,\n" +
            "                \"description\": \"Selecciona la opción ingresar\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"step_id\": 5,\n" +
            "                \"description\": \"Si tu cédula y clave son los correctos, se " +
            "descargará un certificado que indica que tu empleador te ha afiliado, y la fecha " +
            "del" +
            " último aporte al IESS\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"parent_id\": null,\n" +
            "      \"next_step\": null\n" +
            "    }"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = Json.parse(
            Category.serializer(), categoryJson
        )
        setHasOptionsMenu(true)
        println(category)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createMainLayout()
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view as _LinearLayout) {
            verticalLayout {
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
                verticalLayout {
                  loadImage(view)
                    lparams(width = matchParent, height = (heightFormula*2.5).toInt()) {
                        topMargin = -6
                        leftMargin = -6
                        rightMargin = -6
                        bottomMargin= 0
                    }

                    backgroundDrawable =  ContextCompat.getDrawable(
                        context,R.drawable.hearder_info_generic
                    )
                }

                scrollView {
                    verticalLayout {
                        loadInformationDescription()
                        buildSections()
                    }
                    backgroundColor = ContextCompat.getColor(context, R.color.colorBackgroundGenericInfo)
                    lparams(width = matchParent, height = matchParent) {
                        topMargin =  marginTop*2
                        leftMargin = marginLeft*2
                        rightMargin = marginLeft
                        bottomMargin= marginTop*2

                    }
                }





//                params.setMargins(marginLeft, marginTop, 0, 0)
//
//                rl_main_item.layoutParams = params
//
//                textViewCategoryTitle.text = category.subtitle
//
//                val imageView = findViewById<ImageView>(com.untha.R.id.imageView)
//                val imageUrl = resources.getIdentifier(
//                    category.image,
//                    "drawable",
//                    context.applicationInfo.packageName
//                )


                lparams(width = matchParent, height = matchParent) {
                    topPadding = dip(0)
                    bottomPadding = dip(0)
                    leftPadding = dip(0)
                    rightPadding = dip(0)
                }

            }
        }
    }

    private fun createMainLayout(): View {
        return UI {
            verticalLayout {
                backgroundColor = ContextCompat.getColor(context, R.color.colorBackgroundGenericInfo)


                lparams(width = matchParent, height = matchParent) {
                    topPadding = dip(0)
                    bottomPadding = dip(0)
                    leftPadding = dip(0)
                    rightPadding = dip(0)
                }
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildSections() {
        category.information?.sections?.let { sections ->
            sections.map { section ->
                textView {
                    text = section.title

                    textColor =
                        ContextCompat.getColor(
                            context,
                            R.color.colorGenericTitle
                        )
                    setTypeface(typeface, Typeface.BOLD)
                }.lparams(height = wrapContent, width = matchParent) {
                    bottomMargin = dip(bottomMarginMustBeCalculated)
                }
                buildSteps(section)

            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildSteps(
        section: Section
    ) {
        section.steps?.let { steps ->
            steps.map { step ->
                buildRoundedCircleTextView(step, bottomMarginForStepsMustBeCalculated)
                buildStepDescriptionTextView(step, bottomMarginForStepsMustBeCalculated)
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildStepDescriptionTextView(
        step: Step,
        bottomMarginForStepsMustBeCalculated: Int
    ): TextView {
        return textView {
            text = step.description
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(bottomMarginForStepsMustBeCalculated)
        }



    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRoundedCircleTextView(
        step: Step,
        bottomMarginForStepsMustBeCalculated: Int
    ) {
        textView {
            text = step.stepId.toString()
            gravity = Gravity.CENTER
            textColor =
                ContextCompat.getColor(
                    context,
                    android.R.color.white
                )
            setTypeface(typeface, Typeface.BOLD)
            backgroundDrawable = ContextCompat.getDrawable(
                context,
                R.drawable.circular_background
            )

        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(bottomMarginForStepsMustBeCalculated)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadInformationDescription() {
        category.information?.description?.let { description ->
            textView {
                text = description
                textSizeDimen = R.dimen.text_size_content
            }.lparams(height = wrapContent, width = matchParent) {
                bottomMargin = dip(bottomMarginForDescriptionMustBeCalculated)

            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImage(view: View) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
        }
           // .lparams(height = toPixels(131.0, context), width = toPixels(73.0, context)){
            .lparams(height = matchParent, width = matchParent){
                 bottomPadding = toPixels(Constants.PADDING_IMAGE_PLAYER_HEADER.toDouble(), context)
                 topPadding = toPixels(Constants.PADDING_IMAGE_PLAYER_HEADER.toDouble(), context)
            }
    }

    /**
     * Navigates to people details on item click
     */
    override fun onItemClick(category: Category, itemView: View) {
        println("toque el boton")
    }
}
