package com.plcoding.jetpackcomposepokedex.pokemonquiz

import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.plcoding.jetpackcomposepokedex.data.remote.response.Generation
import com.plcoding.jetpackcomposepokedex.ui.theme.darkGreen
import com.plcoding.jetpackcomposepokedex.ui.theme.lightGreen
import com.plcoding.jetpackcomposepokedex.ui.theme.lightRed
import com.plcoding.jetpackcomposepokedex.util.Resource
import com.plcoding.jetpackcomposepokedex.util.calcDominantColor
import com.plcoding.jetpackcomposepokedex.util.parseGenerationToReadableString

@Composable
fun PokemonToGenerationQuiz(
    navController: NavController,
    viewModel: PokemonQuizViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            PokemonToGenerationTopSection(
                navController,
                viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            when (viewModel.pokemonInfo.value) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            dominantColor,
                                            defaultDominantColor
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            PokemonImage(
                                viewModel = viewModel,
                                onColorCalculated = { color ->
                                    dominantColor = color
                                }
                            )
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
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                CorrectOrWrongText(viewModel)
            }
            if (!viewModel.canClick.value) {
                LaunchedEffect(viewModel.isCorrectState.value) {
                    if (viewModel.isCorrectState.value == true) {
                        viewModel.addCorrectAnswer()
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    if (viewModel.isCorrectState.value == false) {
                        ShowCorrectAnswerButton(
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    ShowNextPokemonButton(
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonToGenerationTopSection(
    navController: NavController,
    viewModel: PokemonQuizViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .size(36.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            LinearProgressIndicator(
                progress = viewModel.pokemonGuessed.value.toFloat() / viewModel.pokemonCount.value.toFloat(),
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF639922),
                backgroundColor = Color(0xFFD3D1C7)
            )
            Text(
                text = "${viewModel.pokemonGuessed.value} / ${viewModel.pokemonCount.value}",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun PokemonImage(
    viewModel: PokemonQuizViewModel,
    onColorCalculated: (Color) -> Unit
) {
    val context = LocalContext.current
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
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )
            AsyncImage(
                model = pokemonImageUrl,
                contentDescription = pokemonInfo.data.name,
                modifier = Modifier
                    .size(200.dp),
                onSuccess = { state ->
                    val bitmap = (state.result.drawable as BitmapDrawable).bitmap
                    val drawable = BitmapDrawable(context.resources, bitmap)
                    calcDominantColor(drawable) { color ->
                        onColorCalculated(color)
                    }
                }
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
    val randomGenerations = viewModel.randomGenerations.value
    if (randomGenerations.isNotEmpty()) {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(randomGenerations.chunked(2)) { row ->
                GenerationsRow(viewModel = viewModel, entries = row)
            }
        }
    } else {
        Text(text = "There are not enough generations available.")
    }
}

@Composable
fun GenerationItem(
    viewModel: PokemonQuizViewModel,
    generation: Generation,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val isCorrect = viewModel.generationStates.value[generation.name]
    val borderColor = when (isCorrect) {
        true -> Color.Green
        false -> Color.Red
        else -> Color.LightGray
    }
    val backgroundColor = when (isCorrect) {
        true -> lightGreen
        false -> lightRed
        else -> Color.White
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(100.dp)
            .padding(8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                BorderStroke(2.dp, borderColor),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = viewModel.canClick.value, onClick = onClick)
    ) {
        val gen = parseGenerationToReadableString(generation.name).split("\n").first()
        val region = parseGenerationToReadableString(generation.name).split("\n").last()
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = gen,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
            )
            Spacer(
                modifier = Modifier
                    .size(4.dp)
                    .background(Color.Transparent)
            )
            Text(
                text = region,
                textAlign = TextAlign.Center,
                fontSize = 19.sp,
            )
        }
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
            GenerationItem(
                viewModel = viewModel,
                generation = generation,
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.checkIfCorrectGeneration(generation.name)
                    viewModel.addPokemonCount()
                }
            )
        }
    }
}

@Composable
fun CorrectOrWrongText(
    viewModel: PokemonQuizViewModel
) {
    val isCorrect = viewModel.isCorrectState.value

    val backgroundColor = when (isCorrect) {
        true -> Color(0xFFEAF3DE)
        false -> Color(0xFFFCEBEB)
        null -> Color.Transparent
    }

    val contentColor = when (isCorrect) {
        true -> Color(0xFF27500A)
        false -> Color(0xFF791F1F)
        null -> MaterialTheme.colors.onSurface
    }

    if (isCorrect != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Clear,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp)
            )
            Text(
                text = if (isCorrect) "Correct answer!" else "Wrong answer \uD83D\uDE1E",
                fontSize = 18.sp,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ShowCorrectAnswerButton(
    viewModel: PokemonQuizViewModel,
    modifier: Modifier
) {
    Button(
        onClick = {
            viewModel.showCorrectGeneration()
        },
        border = BorderStroke(
            1.dp,
            color = Color.LightGray,
        ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 1.dp,
            focusedElevation = 2.dp
        ),
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show correct answer",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
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
        colors = ButtonDefaults.buttonColors(
            backgroundColor = darkGreen
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 1.dp,
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
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "ArrowForward",
                tint = Color.White,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(18.dp)
            )
        }
    }
}