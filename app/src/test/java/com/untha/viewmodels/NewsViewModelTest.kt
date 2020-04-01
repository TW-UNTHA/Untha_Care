package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.services.NewsService
import com.untha.model.transactionalmodels.NewsWrapper
import com.untha.utils.Constants
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing

@RunWith(JUnit4::class)
class NewsViewModelTest : KoinTest {
    private val sharedPreferences by inject<SharedPreferences>()
    private val newsService by inject<NewsService>()

    val newsJson = "{\n" +
            "  \"version\": 1,\n" +
            "  \"show_screen\": true,\n" +
            "  \"button_title\": \"ENTENDIDO\",\n" +
            "  \"button_subtitle\": \"PRESIONA AQUÍ Y CONTINÚA HACIA EL APLICATIVO\",\n" +
            "  \"news\": [\n" +
            "    {\n" +
            "      \"id\": 1,\n" +
            "      \"show_new\": true,\n" +
            "      \"title\": \"Estamos en emergencia por el coronavirus.<br>Ten en cuenta lo siguiente:\",\n" +
            "      \"subtitle\": \"(desliza la pantalla hasta el final para continuar)\",\n" +
            "      \"steps\": [\n" +
            "        {\n" +
            "          \"step_id\": 1,\n" +
            "          \"description\": \"No debes exponerte quédate en tu casa. Según el decreto 1017, se suspende actualmente la jornada laboral para todos los trabajadores, incluido las Trabajadoras Remuneradas del Hogar.\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n"

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        startKoin {
            modules(
                listOf(
                    persistenceModule,
                    networkModule,
                    viewModelsModule,
                    mapperModule
                )
            )
        }
        declareMock<SharedPreferences>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should not get news results when news result ids are empty`() {
        val viewModel = NewsViewModel(sharedPreferences, newsService)
        Mockito.`when`(sharedPreferences.getString(Constants.NEWS, "")).thenReturn("")

        viewModel.isScreenVisible()

        verify(sharedPreferences).getString(Constants.NEWS, "")
    }

    @Test
    fun `should return false when news result ids are empty`() {
        val viewModel = NewsViewModel(sharedPreferences, newsService)
        Mockito.`when`(sharedPreferences.getString(Constants.NEWS, "")).thenReturn("")

        val result = viewModel.isScreenVisible()

        assertThat(false, equalTo(result))
        verify(sharedPreferences).getString(Constants.NEWS, "")
    }

    @Test
    fun `should return true when news result ids are not empty`() {
        val viewModel = NewsViewModel(sharedPreferences, newsService)
        Mockito.`when`(sharedPreferences.getString(Constants.NEWS, "")).thenReturn(newsJson)

        val result = viewModel.isScreenVisible()

        assertThat(true, equalTo(result))
        verify(sharedPreferences).getString(Constants.NEWS, "")
    }

    @Test
    fun `should load news from sharePreferences when news result id is not empty`() {
        val viewModel = NewsViewModel(sharedPreferences, newsService)
        val newsExpected = Json.parse(NewsWrapper.serializer(), newsJson)

        Mockito.`when`(sharedPreferences.getString(Constants.NEWS, "")).thenReturn(newsJson)

        viewModel.loadNewsFromSharePreferences()

        assertThat(newsExpected.news, equalTo(viewModel.news))
        assertThat(newsExpected.buttonTitle, equalTo(viewModel.buttonTitle))
        assertThat(newsExpected.buttonSubtitle, equalTo(viewModel.buttonSubtitle))
        assertThat(newsExpected.showScreen, equalTo(viewModel.showScreen))
        verify(sharedPreferences).getString(Constants.NEWS, "")
    }

    @Test
    fun `should save in shared preferences when news result id is not empty`() {
        val routesViewModel = NewsViewModel(
            sharedPreferences,
            newsService
        )
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        val newsExpected = Json.parse(NewsWrapper.serializer(), newsJson)

        whenever(
            editor.putString(
                Constants.NEWS,
                Json.stringify(NewsWrapper.serializer(), newsExpected)
            )
        ).thenReturn(editor)

        doNothing().whenever(editor).apply()

        routesViewModel.saveSharePreferences(newsExpected)

        verify(sharedPreferences.edit())
            .putString(
                Constants.NEWS,
                Json.stringify(NewsWrapper.serializer(), newsExpected)
            )
        verify(editor).apply()

    }
}