package com.plcoding.jetpackcomposepokedex.util

import androidx.compose.ui.graphics.Color
import com.plcoding.jetpackcomposepokedex.data.remote.response.Stat
import com.plcoding.jetpackcomposepokedex.data.remote.response.Type
import com.plcoding.jetpackcomposepokedex.ui.theme.*

fun parseTypeColor(type: Type): Color {
    return when(type.type.name.lowercase(java.util.Locale.ROOT)) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "electric" -> TypeElectric
        "grass" -> TypeGrass
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        else -> Color.Black
    }
}

fun parseStatToColor(stat: Stat): Color {
    return when(stat.stat.name.lowercase()) {
        "hp" -> HPColor
        "attack" -> AtkColor
        "defense" -> DefColor
        "special-attack" -> SpAtkColor
        "special-defense" -> SpDefColor
        "speed" -> SpdColor
        else -> Color.White
    }
}

fun parseStatToAbbr(stat: Stat): String {
    return when(stat.stat.name.lowercase()) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Spd"
        else -> ""
    }
}

fun parseGenerationToReadableString(generation: String?): String {
    return when(generation?.lowercase()) {
        "generation-i" -> "Generation 1\nKanto"
        "generation-ii" -> "Generation 2\nJohto"
        "generation-iii" -> "Generation 3\nHoenn"
        "generation-iv" -> "Generation 4\nSinnoh"
        "generation-v" -> "Generation 5\nUnova"
        "generation-vi" -> "Generation 6\nKalos"
        "generation-vii" -> "Generation 7\nAlola"
        "generation-viii" -> "Generation 8\nGalar"
        "generation-ix" -> "Generation 9\nPaldea"
        else -> ""
    }
}