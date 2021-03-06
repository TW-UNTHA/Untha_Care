package com.untha.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.untha.model.database.ApplicationDatabase
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryRepository
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.repositories.InformationCategoryRepository
import com.untha.model.repositories.SectionRepository
import com.untha.model.repositories.SectionStepRepository
import com.untha.model.services.RetrofitService
import com.untha.viewmodels.AboutUsViewModel
import com.untha.viewmodels.CalculatorFiniquitoInputThreeViewModel
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
import com.untha.viewmodels.CalculatorViewModel
import com.untha.viewmodels.CategoryViewModel
import com.untha.viewmodels.GenericInfoStepViewModel
import com.untha.viewmodels.MainViewModel
import com.untha.viewmodels.MultipleSelectionQuestionViewModel
import com.untha.viewmodels.NewsViewModel
import com.untha.viewmodels.RightsViewModel
import com.untha.viewmodels.RouteResultsViewModel
import com.untha.viewmodels.RoutesViewModel
import com.untha.viewmodels.SingleSelectionQuestionViewModel
import com.untha.viewmodels.UpdateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(),get())}
    viewModel { CategoryViewModel(get(), get(), get()) }
    viewModel { AboutUsViewModel(get()) }
    viewModel { RightsViewModel(get(), get()) }
    viewModel { GenericInfoStepViewModel() }
    viewModel { RoutesViewModel(get()) }
    viewModel { SingleSelectionQuestionViewModel(get()) }
    viewModel { MultipleSelectionQuestionViewModel(get()) }
    viewModel { RouteResultsViewModel(get(), get(), get()) }
    viewModel { UpdateViewModel(get(), get()) }
    viewModel { CalculatorViewModel(get()) }
    viewModel { CalculatorFiniquitoResultsViewModel(get(), get(), get()) }
    viewModel { CalculatorFiniquitoInputThreeViewModel(get()) }
    viewModel { NewsViewModel(get(), get()) }
}

val persistenceModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            ApplicationDatabase::class.java, "application-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { CategoryRepository(get()) }
    factory { InformationCategoryRepository(get()) }
    factory { SectionRepository(get()) }
    factory { SectionStepRepository(get()) }
    factory { CategoryWithRelationsRepository(get()) }

    factory { CategoryDbService(get(), get(), get(), get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            androidContext().packageName,
            Context.MODE_PRIVATE
        )
    }
}

val networkModule = module {
    single { RetrofitService().getRetrofitCategoryService() }
    single { RetrofitService().getRetrofitRouteService() }
    single { RetrofitService().getRetrofitResultService() }
    single { RetrofitService().getRetrofitQuestionnaireRouteResult() }
    single { RetrofitService().getRetrofitVersionService() }
    single { RetrofitService().getRetrofitNewsService() }
    single { RetrofitService().getRetrofitConstantsService() }
}

val mapperModule = module {
    factory { CategoryMapper() }

}
