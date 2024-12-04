package year2024.day1

import getInputResourceLines
import kotlin.math.abs

fun main() {
    val inputRaw = getInputResourceLines(2024, 1)

    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()

    for (line in inputRaw) {
        val (leftVal, rightVal) = line.split(Regex("\\s+"))
        left.add(leftVal.toInt())
        right.add(rightVal.toInt())
    }

    left.sort()
    right.sort()

    val totalDistance = left.zip(right).sumOf { (l, r) -> abs(l - r) }

    println("Total distance: $totalDistance")

    val similarityScore = left.sumOf { leftVal ->
        val occurancesInRight = right.count { rightVal -> rightVal == leftVal }
        leftVal * occurancesInRight
    }

    println("Similarity score: $similarityScore")
}
