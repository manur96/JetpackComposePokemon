package com.plcoding.jetpackcomposepokedex.pokemonquiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.plcoding.jetpackcomposepokedex.data.remote.response.Generation
import com.plcoding.jetpackcomposepokedex.util.Resource
import com.plcoding.jetpackcomposepokedex.util.parseGenerationToReadableString

@Composable
fun PokemonToGenerationQuiz(
    navController: NavController,
    viewModel: PokemonQuizViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(40.dp))
            when (viewModel.pokemonInfo.value) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Box(
                            modifier = Modifier
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                                //.padding(horizontal = 8.dp, vertical = 12.dp)
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    BorderStroke(4.dp, Color.Gray),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            PokemonImage(viewModel)
                        }
                    }
                }

                is Resource.Error -> {
                    Text(text = "Error: ${viewModel.pokemonInfo.value.message}")
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            when (viewModel.pokemonGeneration.value) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    GenerationsToChoose(viewModel)
                }

                is Resource.Error -> {
                    Text(text = "Error: ${viewModel.pokemonGeneration.value.message}")
                }
            }
        }
    }
}

@Composable
fun PokemonImage(viewModel: PokemonQuizViewModel) {
    val pokemonInfo = viewModel.pokemonInfo.value

    val pokemonImageUrl = pokemonInfo.data?.sprites?.front_default
    if (pokemonInfo is Resource.Success && pokemonImageUrl != null) {
        Column {
            Text(
                text = pokemonInfo.data.name
                    .replace("-", " ")
                    .replaceFirstChar {
                        it.uppercase()
                    },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )
            AsyncImage(
                model = pokemonImageUrl,
                contentDescription = pokemonInfo.data.name,
                modifier = Modifier
                    .size(200.dp)
            )
        }
    } else if (pokemonInfo is Resource.Loading) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
    } else if (pokemonInfo is Resource.Error) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Error: ${pokemonInfo.message}",
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun GenerationsToChoose(
    viewModel: PokemonQuizViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.fetchAllGenerations()
    }

    when (val generationsResponse = viewModel.generations.value) {
        is Resource.Loading -> {
            CircularProgressIndicator()
        }

        is Resource.Success -> {
            val allGenerations = generationsResponse.data?.results ?: emptyList()
            val filteredGenerations = allGenerations.filter {
                it.name != viewModel.pokemonGeneration.value.data?.generation?.name
            }
            if (filteredGenerations.size >= 3) {
                val randomGenerations = filteredGenerations.shuffled().take(3)
                val generationsToShow = randomGenerations + Generation(
                    viewModel.pokemonGeneration.value.data?.generation?.name ?: "", ""
                )
                val shuffledGenerations = generationsToShow.shuffled()

                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(shuffledGenerations.chunked(2)) { row ->
                        GenerationsRow(
                            viewModel = viewModel,
                            entries = row
                        )
                    }
                }
            } else {
                Text(text = "There are not enough generations available.")
            }
        }

        is Resource.Error -> {
            Text(text = "Error: ${generationsResponse.message}")
        }
    }
}

@Composable
fun GenerationItem(
    viewModel: PokemonQuizViewModel,
    generation: Generation,
    isCorrect: Boolean?,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val borderColor = when (isCorrect) {
        true -> Color.Green
        false -> Color.Red
        else -> Color.Black
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(100.dp)
            .padding(8.dp)
            .background(
                color = Color(0xFFF0F0F0),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                BorderStroke(2.dp, borderColor),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = viewModel.canClick.value,
                onClick = onClick
            )
    ) {
        Text(
            text = parseGenerationToReadableString(generation.name),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GenerationsRow(
    viewModel: PokemonQuizViewModel,
    entries: List<Generation>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        entries.forEach { generation ->
            val isCorrectState = remember { mutableStateOf<Boolean?>(null) }
            GenerationItem(
                viewModel = viewModel,
                generation = generation,
                isCorrect = isCorrectState.value,
                modifier = Modifier.weight(1f),
                onClick = {
                    isCorrectState.value = viewModel.checkIfCorrectGeneration(generation.name)
                }
            )
        }
    }
}