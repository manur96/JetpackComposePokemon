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
        "generation-i" -> "Generation 1\n(Kanto)"
        "generation-ii" -> "Generation 2\n(Johto)"
        "generation-iii" -> "Generation 3\n(Hoenn)"
        "generation-iv" -> "Generation 4\n(Sinnoh)"
        "generation-v" -> "Generation 5\n(Unova)"
        "generation-vi" -> "Generation 6\n(Kalos)"
        "generation-vii" -> "Generation 7\n(Alola)"
        "generation-viii" -> "Generation 8\n(Galar)"
        "generation-ix" -> "Generation 9\n(Paldea)"
        else -> ""
    }
}