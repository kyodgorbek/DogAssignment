package ceo.dog.dogapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import ceo.dog.dogapp.di.BindingModuleActivities
import ceo.dog.dogapp.di.DogComponent
import ceo.dog.dogapp.di.NetworkModule
import ceo.dog.dogapp.di.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

class TestApplication : DogApp()
/**
 * A custom [AndroidJUnitRunner] used to replace the application used in tests with a
 * [TestApplication].
 */
class CustomTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        TestRepositoryModule::class,
        NetworkModule::class,
        BindingModuleActivities::class,
        ViewModelModule::class
    ]
)
interface TestAppComponent : DogComponent
