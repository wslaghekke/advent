package year2023.day9

import getInputResourceLines

fun main() {
    val lines = getInputResourceLines(2023, 9)
        .map { line -> line.split(" ").map { it.toInt() } }

    val nextNumberSum = lines.map { line ->
        val next = calculateNextNumberInSeries(line)
        println("Next: $next")
        next
    }.sum()

    println("Total: $nextNumberSum")


    val prevNumberSum = lines.map { line ->
        val next = calculateNextNumberInSeries(line.reversed())
        println("Prev: $next")
        next
    }.sum()

    println("Total: $prevNumberSum")
}

fun calculateNextNumberInSeries(series: List<Int>): Int {
    var allZero = true
    val nextSeries = series.windowed(2, 1).map { (a,b) ->
        val result = b - a;
        if (result != 0) allZero = false
        result
    }

    return if (allZero) {
        return series.last()
    } else {
        val next = calculateNextNumberInSeries(nextSeries)
        series.last() + next
    }
}
