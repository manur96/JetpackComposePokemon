package com.plcoding.jetpackcomposepokedex.data.remote.response

data class PokemonSpeciesResponse(
    val response: List<Species>,
    val generation: Generation
)

data class GenerationResponse(
    val results: List<Generation>
)

data class Generation(
    val name: String,
    val url: String
)
