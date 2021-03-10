package ceo.dog.dogapp

import ceo.dog.dogapp.data.Resource
import ceo.dog.dogapp.domain.Repository
import ceo.dog.dogapp.domain.model.Breed
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@Module
class TestRepositoryModule(
    private val repositoryModule: Repository = object : Repository {
        override suspend fun getAllBreed(): Flow<Resource<List<Breed>>> = flowOf(Resource.success(listOf()))

        override suspend fun getBreedImages(breed: String): Flow<Resource<List<String>>> = flowOf(Resource.success(listOf()))
    }
) {
    @Provides
    @Singleton
    fun provideRepository(): Repository = repositoryModule
}