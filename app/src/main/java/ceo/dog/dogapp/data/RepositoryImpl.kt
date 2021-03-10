package ceo.dog.dogapp.data

import ceo.dog.dogapp.domain.Repository
import ceo.dog.dogapp.domain.model.Breed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: DogApiService,
    private val dispatcher: CoroutineDispatcher
) : Repository {
    override suspend fun getAllBreed(): Flow<Resource<List<Breed>>> = flow {
        emit(call<Map<String, List<String>>> { api.fetchAllBreed() })
    }.map { it.mapValue { it?.mapToBreed() } }
        .flowOn(dispatcher)

    override suspend fun getBreedImages(breed: String): Flow<Resource<List<String>>> = flow {
        emit(call<List<String>> { api.getImagesByBreed(breed) })
    }
        .flowOn(dispatcher)
}

private fun Map<String, List<String>>.mapToBreed(): List<Breed> {
    val list = mutableListOf<Breed>()
    forEach { (key, value) ->
        list.add(Breed(key, value.map { Breed(it, emptyList()) }))
    }
    return list
}