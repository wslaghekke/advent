package year2024.day5

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2024, 5)

    val sortPairs: MutableList<Pair<Int, Int>> = mutableListOf()
    val pages: MutableList<List<Int>> = mutableListOf()
    var allPairsProcessed = false
    for (line in inputRaw) {
        if (line == "") {
            allPairsProcessed = true
            continue
        }

        if (!allPairsProcessed) {
            val (left, right) = line.split('|').map { it.toInt() }
            sortPairs.add(Pair(left, right))
        } else {
            val page = line.split(',').map { it.toInt() }
            pages.add(page)
        }
    }

    val correctedPages = mutableListOf<List<Int>>()
    val correctPages = pages.filter { page ->
        var sortCalls = 0
        val sortedPage = page.sortedWith { a, b ->
            sortCalls++
            val matchingPair = sortPairs.find { (left, right) -> left == a && right == b || left == b && right == a }
            if (matchingPair === null) {
                0
            } else {
                if (matchingPair.first == a) {
                    -1
                } else {
                    1
                }
            }
        }

        val valid = page == sortedPage
        if (!valid) {
            correctedPages.add(sortedPage)
        }
        println("Page: $page, Sorted: $sortedPage, Valid: $valid, Page length: ${page.size} Sort calls: $sortCalls")
        valid
    }

    val correctPagesMiddleNumber = correctPages.sumOf { it[it.size / 2] }
    println("Part 1: $correctPagesMiddleNumber")

    val correctedPagesMiddleNumber = correctedPages.sumOf { it[it.size / 2] }
    println("Part 2: $correctedPagesMiddleNumber")
}
