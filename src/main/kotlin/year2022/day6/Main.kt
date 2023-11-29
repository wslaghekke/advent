package year2022.day6

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2022, 6)

    for (line in inputRaw) {
        val markerSize = 14;
        val firstMarkerIndex = line.windowedSequence(markerSize) { charSequence ->
            charSequence.toSet().size == markerSize
        }.indexOfFirst { it } + markerSize

        println("First marker after char $firstMarkerIndex")

        //
    }
}
