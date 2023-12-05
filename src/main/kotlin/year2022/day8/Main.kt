package year2022.day8

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2022, 8)
    val matrix: List<List<Int>> = inputRaw.map {
        it.map { char -> Integer.parseInt(char.toString()) }
    }
}
