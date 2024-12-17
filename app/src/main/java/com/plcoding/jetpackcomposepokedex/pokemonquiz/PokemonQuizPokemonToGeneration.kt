package com.plcoding.jetpackcomposepokedex.pokemonquiz

import android.net.Proxy
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
            PokemonToGenerationTopSection(
                navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
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
            Spacer(modifier = Modifier.height(20.dp))
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                CorrectOrWrongText(viewModel)
            }
            if (!viewModel.canClick.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (viewModel.isCorrectState.value == false) {
                        ShowCorrectAnswerButton(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                    ShowNextPokemonButton(
                        viewModel = viewModel,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonToGenerationTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Color.Transparent
            )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
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

@Composable
fun CorrectOrWrongText(
    viewModel: PokemonQuizViewModel
) {
    when (viewModel.isCorrectState.value) {
        true -> Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Check",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(18.dp)
            )
            Text(
                text = "Correct answer!",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
        false -> Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(18.dp)
            )
            Text(
                text = "Wrong answer ${"\uD83D\uDE1E"}",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
        null -> Row { }
    }
}

@Composable
fun ShowCorrectAnswerButton(
    modifier: Modifier
) {
    Button(
        onClick = { },
        border = BorderStroke(
            2.dp,
            color = Color.Black
        ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp,
            focusedElevation = 2.dp
        ),
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Text(
            text = "Show correct answer",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun ShowNextPokemonButton(
    viewModel: PokemonQuizViewModel,
    modifier: Modifier
) {
    Button(
        onClick = {
            viewModel.fetchNextPokemon()
        },
        border = BorderStroke(
            2.dp,
            color = Color.Black
        ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp,
            focusedElevation = 2.dp
        ),
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Next Pokémon",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "ArrowForward",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(18.dp)
            )
        }
    }
}