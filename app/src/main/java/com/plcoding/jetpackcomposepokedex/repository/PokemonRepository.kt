package com.plcoding.jetpackcomposepokedex.repository

import com.plcoding.jetpackcomposepokedex.data.remote.PokeAPI
import com.plcoding.jetpackcomposepokedex.data.remote.response.GenerationResponse
import com.plcoding.jetpackcomposepokedex.data.remote.response.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.response.PokemonList
import com.plcoding.jetpackcomposepokedex.data.remote.response.PokemonSpeciesResponse
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeAPI
) {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfoWithName(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfoWithName(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred while fetching pokemon ${pokemonName}.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfoWithNumber(pokemonNumber: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfoWithNumber(pokemonNumber)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred while fetching pokemon ${pokemonNumber}.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonSpeciesCount(): Resource<Int> {
        val response = try {
            api.getPokemonSpecies().count
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred while fetching pokemon count.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonGeneration(pokemonNumber: String): Resource<PokemonSpeciesResponse> {
        val response = try {
            api.getPokemonSpeciesInfo(pokemonNumber)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred while fetching the generation of the pokemon ${pokemonNumber}.")
        }
        return Resource.Success(response)
    }
    suspend fun getAllGenerations(): Resource<GenerationResponse> {
        return try {
            val response = api.getAllGenerations()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred while fetching all generations.")
        }
    }
}