package year2024.day3

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2024, 3).joinToString(" ")

    var mulEnabled = true
    val mulSum = Regex("do\\(\\)|don't\\(\\)|mul\\((\\d+),(\\d+)\\)")
        .findAll(inputRaw)
        .map {
            when (it.groupValues[0]) {
                "do()" -> mulEnabled = true
                "don't()" -> mulEnabled = false
                else ->  {
                    if (mulEnabled) {
                        return@map it.groupValues[1].toInt() * it.groupValues[2].toInt()
                    }
                }
            }

            0
        }
        .sum()

    println("mul sum: $mulSum")
}
