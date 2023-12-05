package year2023.day5

import getInputResourceLines
import java.util.concurrent.Executors

fun main() {
    val startTime = System.nanoTime()
    val lines = getInputResourceLines(2023, 5).toMutableList()

    val seeds = lines.removeFirst().removePrefix("seeds: ").split(" ").mapNotNull { it.toLongOrNull() }
    val maps = mutableListOf<MutableList<NumberMapRange>>()
    var lastMapIndex = 0;

    for (line in lines) {
        if (line.isBlank()) continue
        if (line[0].isLetter()) {
            maps.add(mutableListOf())
            lastMapIndex = maps.size - 1
        } else {
            val (outRangeStart, inRangeStart, rangeLen) = line.split(" ").map { it.toLong() }
            maps[lastMapIndex].add(NumberMapRange(outRangeStart, inRangeStart, rangeLen))
        }
    }

    val results = seeds.map { seed ->
        val seedChanges = mutableListOf(seed)
        var result = seed
        for (map in maps) {
            result = map.find { range -> range.inputInRange(result) }?.let {
                it.outputForInput(result)
            } ?: result
            seedChanges.add(result)

        }
        println(seedChanges.joinToString(" -> "))
        result
    }

    println("Lowest result: ${results.minOrNull()}")
    println("Took ${(System.nanoTime() - startTime) / 1_000_000}ms")

    val part2StartTime = System.nanoTime()

    val expandedSeedRanges = seeds.chunked(2).flatMap { (startNum, rangeLen) ->
        val maxRangeLength = 10_000_000
        if (rangeLen > maxRangeLength) {
            // Divide into smaller ranges of max maxRangeLength
            val numRanges = rangeLen / maxRangeLength
            val ranges = mutableListOf<LongRange>()
            var nextRangeStart = startNum
            for (i in 0..<numRanges) {
                ranges.add(nextRangeStart..<(nextRangeStart + maxRangeLength))
                nextRangeStart += maxRangeLength
            }

            // Add the remainder
            if (rangeLen % maxRangeLength != 0L) {
                ranges.add(nextRangeStart..<(nextRangeStart + rangeLen % maxRangeLength))
            }

            ranges
        } else {
            listOf(startNum..<(startNum + rangeLen))
        }
    }
    val threads = Executors.newVirtualThreadPerTaskExecutor()

    val minPerThread = expandedSeedRanges.map {
        threads.submit<Long> {
            println("Started thread for seeds ${it.first} - ${it.last}")
            it.minOf { seed ->
                var result = seed
                for (map in maps) {
                    result = map.find { range -> range.inputInRange(result) }?.let {
                        it.outputForInput(result)
                    } ?: result
                }
                result
            }
        }
    }

    val minExpandedSeed = minPerThread.minOfOrNull { it.get() }

    println("Lowest expanded result: $minExpandedSeed")
    println("Took ${(System.nanoTime() - part2StartTime) / 1_000_000}ms")
}

data class NumberMapRange(val outRangeStart: Long, val inRangeStart: Long, val rangeLen: Long) {
    val rangeEnd = inRangeStart + rangeLen
    fun inputInRange(input: Long): Boolean = input >= inRangeStart && input < rangeEnd
    fun outputForInput(input: Long): Long {
        return input - inRangeStart + outRangeStart
    }
}
