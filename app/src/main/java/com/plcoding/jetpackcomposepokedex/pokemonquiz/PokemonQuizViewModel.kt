package com.plcoding.jetpackcomposepokedex.pokemonquiz

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.jetpackcomposepokedex.data.remote.response.GenerationResponse
import com.plcoding.jetpackcomposepokedex.data.remote.response.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.response.PokemonSpeciesResponse
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PokemonQuizViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    var pokemonInfo = mutableStateOf<Resource<Pokemon>>(Resource.Loading())
    var pokemonGeneration = mutableStateOf<Resource<PokemonSpeciesResponse>>(Resource.Loading())
    var generations = mutableStateOf<Resource<GenerationResponse>>(Resource.Loading())

    init {
        fetchRandomPokemon()
    }

    fun fetchAllGenerations() {
        viewModelScope.launch {
            generations.value = repository.getAllGenerations()
        }
    }

    private fun fetchRandomPokemon() {
        viewModelScope.launch {
            val randomNumber = Random.nextInt(1, getPokemonCount())
            pokemonInfo.value = getPokemonInfoWithNumber(randomNumber.toString())

            if (pokemonInfo.value is Resource.Success) {
                val pokemon = (pokemonInfo.value as Resource.Success<Pokemon>).data
                fetchPokemonGeneration(pokemon?.id.toString())
            }
        }
    }

    private suspend fun getPokemonInfoWithNumber(pokemonNumber: String): Resource<Pokemon> {
        return repository.getPokemonInfoWithNumber(pokemonNumber)
    }

    private suspend fun getPokemonCount(): Int {
        return repository.getPokemonSpeciesCount().data ?: 0 // Manejar potenciales null
    }

    private fun fetchPokemonGeneration(pokemonNumber: String) {
        viewModelScope.launch {
            pokemonGeneration.value = repository.getPokemonGeneration(pokemonNumber)
        }
    }
}