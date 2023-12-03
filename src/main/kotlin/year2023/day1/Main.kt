package year2023.day1

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2023, 1)

    val digitRegex = Regex("[0-9]|one|two|three|four|five|six|seven|eight|nine")
    val calibrationValues = inputRaw.map { line ->
        val digits = (0..line.length).mapNotNull { digitRegex.matchAt(line, it) }.map {
            when (it.value) {
                "1", "one" -> 1
                "2", "two" -> 2
                "3", "three" -> 3
                "4", "four" -> 4
                "5", "five" -> 5
                "6", "six" -> 6
                "7", "seven" -> 7
                "8", "eight" -> 8
                "9", "nine" -> 9
                else -> throw Exception("Unknown digit: ${it.value}")
            }
        }.toList()
        println("$line -> $digits")

        digits.first() * 10 + digits.last()
    }

    println("Total calibration value: ${calibrationValues.sum()}")
}
