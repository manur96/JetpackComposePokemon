package com.plcoding.jetpackcomposepokedex.data.remote

import com.plcoding.jetpackcomposepokedex.data.remote.response.GenerationResponse
import com.plcoding.jetpackcomposepokedex.data.remote.response.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.response.PokemonList
import com.plcoding.jetpackcomposepokedex.data.remote.response.PokemonSpeciesResponse
import com.plcoding.jetpackcomposepokedex.data.remote.response.SpeciesList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeAPI {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfoWithName(
        @Path("name") name: String
    ): Pokemon

    @GET("pokemon/{number}")
    suspend fun getPokemonInfoWithNumber(
        @Path("number") number: String
    ): Pokemon

    @GET("pokemon-species")
    suspend fun getPokemonSpecies(): SpeciesList

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpeciesInfo(
        @Path("id") id: String
    ): PokemonSpeciesResponse

    @GET("generation")
    suspend fun getAllGenerations(): GenerationResponse
}