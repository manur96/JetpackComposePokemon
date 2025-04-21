package com.plcoding.jetpackcomposepokedex.pokemonquiz

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.jetpackcomposepokedex.data.remote.response.Generation
import com.plcoding.jetpackcomposepokedex.data.remote.response.GenerationResponse
import com.plcoding.jetpackcomposepokedex.data.remote.response.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.response.PokemonSpeciesResponse
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PokemonQuizViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonInfo = mutableStateOf<Resource<Pokemon>>(Resource.Loading())
    val pokemonInfo: State<Resource<Pokemon>> = _pokemonInfo

    private val _pokemonGeneration = mutableStateOf<Resource<PokemonSpeciesResponse>>(Resource.Loading())
    val pokemonGeneration: State<Resource<PokemonSpeciesResponse>> = _pokemonGeneration

    private val _generations = mutableStateOf<Resource<GenerationResponse>>(Resource.Loading())
    val generations: State<Resource<GenerationResponse>> = _generations

    private val _canClick = mutableStateOf(true)
    val canClick: State<Boolean> = _canClick

    private val _isCorrectState = mutableStateOf<Boolean?>(null)
    val isCorrectState: State<Boolean?> = _isCorrectState

    private val _randomGenerations = mutableStateOf<List<Generation>>(emptyList())
    val randomGenerations: State<List<Generation>> = _randomGenerations

    private val _generationStates = mutableStateOf<Map<String, Boolean?>>(emptyMap())
    val generationStates: State<Map<String, Boolean?>> = _generationStates

    private val _pokemonGuessed = mutableIntStateOf(0)
    val pokemonGuessed: State<Int> = _pokemonGuessed

    private val _pokemonCount = mutableIntStateOf(0)
    val pokemonCount: State<Int> = _pokemonCount

    init {
        fetchRandomPokemon()
        fetchAllGenerations()
    }

    fun checkIfCorrectGeneration(selectedGeneration: String): Boolean? {
        _canClick.value = false
        val isCorrect = selectedGeneration == pokemonGeneration.value.data?.generation?.name
        _generationStates.value = _generationStates.value.toMutableMap().apply {
            put(selectedGeneration, isCorrect)
        }
        _isCorrectState.value = isCorrect
        return isCorrect
    }

    fun fetchNextPokemon() {
        _canClick.value = true
        _isCorrectState.value = null
        _generationStates.value = emptyMap()
        fetchRandomPokemon()
    }

    private fun fetchAllGenerations() {
        viewModelScope.launch {
            _generations.value = repository.getAllGenerations()

            if (_generations.value is Resource.Success) {
                val allGenerations = _generations.value.data?.results ?: emptyList()
                val currentGenerationName = _pokemonGeneration.value.data?.generation?.name
                val filteredGenerations = allGenerations.filter { it.name != currentGenerationName }

                if (filteredGenerations.size >= 3) {
                    val generationsToShow = filteredGenerations.shuffled().take(3) + Generation(
                        currentGenerationName ?: "", _pokemonGeneration.value.data?.generation?.url ?: ""
                    )
                    _randomGenerations.value = generationsToShow.shuffled()
                }
            }
        }
    }

    private fun fetchRandomPokemon() {
        viewModelScope.launch {
            val pokemonCount = getPokemonCount()
            val randomNumber = Random.nextInt(1, pokemonCount + 1)
            _pokemonInfo.value = getPokemonInfoWithNumber(randomNumber.toString())

            if (_pokemonInfo.value is Resource.Success) {
                val pokemon = (_pokemonInfo.value as Resource.Success<Pokemon>).data
                fetchPokemonGeneration(pokemon?.id.toString())
            }
        }
    }

    private suspend fun getPokemonInfoWithNumber(pokemonNumber: String): Resource<Pokemon> {
        return repository.getPokemonInfoWithNumber(pokemonNumber)
    }

    private suspend fun getPokemonCount(): Int {
        return repository.getPokemonSpeciesCount().data ?: 0
    }

    private fun fetchPokemonGeneration(pokemonNumber: String) {
        viewModelScope.launch {
            _pokemonGeneration.value = repository.getPokemonGeneration(pokemonNumber)
            fetchAllGenerations()
        }
    }

    fun showCorrectGeneration() {
        val correctGeneration = pokemonGeneration.value.data?.generation?.name
        if (correctGeneration != null) {
            _generationStates.value = _generationStates.value.toMutableMap().apply {
                put(correctGeneration, true)
            }
        }
    }

    fun addCorrectAnswer() {
        _pokemonGuessed.intValue = _pokemonGuessed.intValue + 1
    }

    fun addPokemonCount() {
        _pokemonCount.intValue = _pokemonCount.intValue + 1
    }
}