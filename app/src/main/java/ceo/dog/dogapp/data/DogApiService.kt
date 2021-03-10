package ceo.dog.dogapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {
    @GET("breeds/list/all")
    suspend fun fetchAllBreed(): AllBreedResponse

    @GET("breed/{breed}/images")
    suspend fun getImagesByBreed(@Path("breed") breed: String): BreedImagesResponse
}