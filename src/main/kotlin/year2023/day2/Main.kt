package year2023.day2

import getInputResourceLines
import kotlin.math.max

fun main() {
     val inputRaw = getInputResourceLines(2023, 2)
    val games = inputRaw.map { line ->
        val (gameId, revealedBoxes) = line.split(":")
        val revealedSets = revealedBoxes.split(";").map { set ->
            var red = 0;
            var green = 0;
            var blue = 0;

            set.split(",").forEach { item ->
                val trimmed = item.trim()
                when {
                    trimmed.endsWith("red") -> {
                        red += trimmed.removeSuffix(" red").toInt()
                    }
                    trimmed.endsWith("green") -> {
                        green += trimmed.removeSuffix(" green").toInt()
                    }
                    trimmed.endsWith("blue") -> {
                        blue += trimmed.removeSuffix(" blue").toInt()
                    }
                }
            }

            RevealedSet(red, green, blue)
        }

        Game(gameId.removePrefix("Game ").toInt(), revealedSets)
    }

    val possibleGames = games.filter { it.isPossibleWith(12,13,14) }
    println("Total possible ids: ${possibleGames.sumOf { it.gameId }}")

    val fewestCubePowers = games.map {
        val (red,green,blue) = it.revealedSets.fold(Triple(0,0,0)) { (red,green,blue), revealedSet ->
            Triple(
                max(red, revealedSet.red),
                max(green, revealedSet.green),
                max(blue, revealedSet.blue)
            )
        }
        red * green * blue
    }

    println("Total fewestCubePower sum: ${fewestCubePowers.sum()}")

}

data class Game(
    val gameId: Int,
    val revealedSets: List<RevealedSet>
) {
    fun isPossibleWith(totalRed: Int, totalGreen: Int, totalBlue: Int): Boolean {
        return revealedSets.all { it.isPossibleWith(totalRed, totalGreen, totalBlue) }
    }
}

data class RevealedSet(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    fun isPossibleWith(totalRed: Int, totalGreen: Int, totalBlue: Int): Boolean {
        return red <= totalRed && green <= totalGreen && blue <= totalBlue
    }
}
