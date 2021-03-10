package ceo.dog.dogapp.domain.model

data class Breed(val name: String, val subBreed: List<Breed> = emptyList())