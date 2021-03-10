package ceo.dog.dogapp.di

import ceo.dog.dogapp.DogApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        RepositoryModule::class,
        NetworkModule::class,
        BindingModuleActivities::class,
        ViewModelModule::class
    ]
)
interface DogComponent : AndroidInjector<DogApp>