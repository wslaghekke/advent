package year2023.day6

import getInputResourceLines

fun main() {
    val lines = getInputResourceLines(2023, 6)
    val times = lines[0].removePrefix("Time:").split(" ").mapNotNull { it.toIntOrNull() }
    val distances = lines[1].removePrefix("Distance:").split(" ").mapNotNull { it.toIntOrNull() }

    val raceWinPossibilityCount = times.mapIndexed { index, time ->
        val distanceToBeat = distances[index]
        // Determine possible button hold times per race
        val possibleHoldTimes = (1..<time).count { holdTime ->
            // Calculate distance for holdTime
            val remainingTime = time - holdTime
            // Distance traveled
            val distanceTraveled = remainingTime * holdTime
            distanceTraveled > distanceToBeat
        }

        println("Race $index: $possibleHoldTimes")
        possibleHoldTimes
    }
    val raceWinProduct = raceWinPossibilityCount.reduce { result, item -> result * item }

    println("Product of possibilities: $raceWinProduct")

    val singleRaceTime = times.joinToString("").toLong()
    val singleRaceDistance = distances.joinToString("").toLong()

    // Determine possible button hold times per race
    val possibleHoldTimes = (1..<singleRaceTime).count { holdTime ->
        // Calculate distance for holdTime
        val remainingTime = singleRaceTime - holdTime
        // Distance traveled
        val distanceTraveled = remainingTime * holdTime
        distanceTraveled > singleRaceDistance
    }

    println("Possibilities in single race: $possibleHoldTimes")
}
