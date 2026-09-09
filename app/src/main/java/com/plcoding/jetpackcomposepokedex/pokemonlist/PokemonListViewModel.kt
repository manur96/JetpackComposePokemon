package com.plcoding.jetpackcomposepokedex.pokemonlist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.jetpackcomposepokedex.data.models.PokedexListEntry
import com.plcoding.jetpackcomposepokedex.data.remote.response.Result
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.util.Constants.PAGE_SIZE
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {
    private var currentPage = 0
    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var allPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
        loadAllPokemonNames()
    }

    private fun loadAllPokemonNames() {
        viewModelScope.launch {
            when (val result = repository.getPokemonList(2000, 0)) {
                is Resource.Success -> {
                    allPokemonList = mapToPokedexEntries(result.data!!.results)
                }
                is Resource.Error -> {
                    Log.ERROR
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            when (val result = repository.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)) {
                is Resource.Success -> {
                    endReached.value = currentPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = mapToPokedexEntries(result.data.results)
                    currentPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message.toString()
                    isLoading.value = false
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun searchPokemonList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            if (isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            val results = allPokemonList.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true)
                        || it.number.toString() == query.trim()
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    private fun mapToPokedexEntries(
        results: List<Result>
    ): List<PokedexListEntry> {
        return results.mapIndexed { index, entry ->
            val numberOfPokemon = if (entry.url.endsWith("/")) {
                entry.url.dropLast(1).takeLastWhile { it.isDigit() }
            } else {
                entry.url.takeLastWhile { it.isDigit() }
            }
            val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${numberOfPokemon}.png"
            PokedexListEntry(
                entry.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                },
                url,
                numberOfPokemon.toInt()
            )
        }
    }
}