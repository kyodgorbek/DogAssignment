package ceo.dog.dogapp.di

import ceo.dog.dogapp.domain.Repository
import ceo.dog.dogapp.data.RepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(repo: RepositoryImpl): Repository = repo
}