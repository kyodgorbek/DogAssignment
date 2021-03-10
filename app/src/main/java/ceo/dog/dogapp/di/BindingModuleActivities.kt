package ceo.dog.dogapp.di

import ceo.dog.dogapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BindingModuleActivities {
    @ContributesAndroidInjector
    abstract fun contributeActivity(): MainActivity
}