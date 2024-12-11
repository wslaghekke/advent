package year2024.day11

import getInputResourceLines

val cachedStoneCount = mutableMapOf<Pair<Long, Int>, Long>()

fun main() {
    val inputRaw = getInputResourceLines(2024, 11)
    val stones = inputRaw.first().split(' ').map { it.toLong() }

    val resultingStoneCount = stones.sumOf { transformStone(it, 25) }

    println("Number of stones after 25 transformations: $resultingStoneCount")

    val resultingStoneCount2 = stones.sumOf { transformStone(it, 75) }

    println("Number of stones after 75 transformations: $resultingStoneCount2")
}

fun transformStone(stone: Long, times: Int): Long {
    if (times == 0) {
        return 1
    }

    val cachedCount = cachedStoneCount[stone to times]
    if (cachedCount != null) {
        return cachedCount
    }

    val result = if (stone == 0L) {
        transformStone(1, times - 1)
    } else {
        val stoneStr = stone.toString()
        if (stoneStr.length % 2 == 0) {
            val left = stoneStr.substring(0..<stoneStr.length / 2).toLong()
            val right = stoneStr.substring(stoneStr.length / 2..<stoneStr.length).toLong()

            transformStone(left, times - 1) + transformStone(right, times - 1)
        } else {
            transformStone(stone * 2024, times - 1)
        }
    }

    cachedStoneCount[stone to times] = result
    return result
}
