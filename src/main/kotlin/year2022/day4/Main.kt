package year2022.day4

import getInputResourceLines

fun main(args: Array<String>) {
    val inputRaw = getInputResourceLines(2022, 4)

    // Input format:
    // 2-4,6-8 -> [2,3,4] to [6,7,8]
    val assignmentPairs = inputRaw.map { line ->
        val (first, second) = line.split(",")
        expandRange(first) to expandRange(second)
    }

    val fullyOverlappingCount = assignmentPairs.count { (first, second) ->
        // first contained in second
        val overlaps = if (first.first >= second.first && first.last <= second.last) {
            true
        } else if (second.first >= first.first && second.last <= first.last) {
            true
        } else {
            false
        }
        if (overlaps) {
            println("Overlap: $first - $second")
        }

        overlaps
    }

    println("Overlap count: $fullyOverlappingCount")
}

fun expandRange(range: String): IntRange {
    val (start, end) = range.split("-")
    return (start.toInt()..end.toInt())
}
