package ceo.dog.dogapp.domain

import ceo.dog.dogapp.data.Resource
import ceo.dog.dogapp.domain.model.Breed
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getAllBreed(): Flow<Resource<List<Breed>>>

    suspend fun getBreedImages(breed: String): Flow<Resource<List<String>>>
}