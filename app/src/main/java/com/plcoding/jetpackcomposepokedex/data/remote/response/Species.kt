package com.plcoding.jetpackcomposepokedex.data.remote.response

data class SpeciesList(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Species>
)

data class Species(
    val name: String,
    val url: String
)