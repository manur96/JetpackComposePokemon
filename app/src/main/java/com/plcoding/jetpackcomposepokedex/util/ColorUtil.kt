package com.plcoding.jetpackcomposepokedex.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
    val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
    Palette.from(bitmap).generate { palette ->
        val totalPopulation = palette?.swatches?.sumOf { it.population } ?: 1

        val candidates = listOfNotNull(
            palette?.vibrantSwatch,
            palette?.lightVibrantSwatch,
            palette?.darkVibrantSwatch,
            palette?.dominantSwatch
        )

        val swatch = candidates.firstOrNull { swatch ->
            swatch.population.toFloat() / totalPopulation > 0.1f
        } ?: candidates.firstOrNull()

        swatch?.rgb?.let { colorValue ->
            onFinish(Color(colorValue))
        }
    }
}