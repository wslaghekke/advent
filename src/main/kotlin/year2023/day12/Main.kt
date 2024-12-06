package year2023.day12

import getInputResourceLines
import java.util.concurrent.Executors

fun main() {
    val lines = getInputResourceLines(2023, 12)

    var part1Total = 0L
    for (line in lines) {
        val (inputStr, springGroupStr) = line.split(" ")
        val springGroups = springGroupStr.split(",").map { it.toInt() }

        val total = calculatePossibilities(
            inputStr,
            springGroups,
//            "",
//            springGroups
        );
        part1Total += total
        //println("Total:  $inputStr $springGroups: $total")
        //println("====================================")
    }

    println("Part 1 total: $part1Total")

    val part2StartTime = System.nanoTime()

    val threads = Executors.newVirtualThreadPerTaskExecutor()

    val part2Futures = lines.map { line ->
        threads.submit<Long> {
            val (inputStr, springGroupStr) = line.split(" ")
            val springGroups = springGroupStr.split(",").map { it.toInt() }


            val expandedInputBuilder = StringBuilder()
            val expandedSpringGroups = mutableListOf<Int>()
            repeat(5) {
                expandedInputBuilder.append(inputStr)
                if (it < 4) {
                    // Add question mark between input strings (but not after last one)
                    expandedInputBuilder.append("?")
                }
                expandedSpringGroups.addAll(springGroups)
            }
            val expandedInputStr = expandedInputBuilder.toString()

            println("Input:    $inputStr          $springGroups")
            println("Expanded: $expandedInputStr  $expandedSpringGroups")

            val total = calculatePossibilities(
                expandedInputStr,
                expandedSpringGroups,
//            "",
//            expandedSpringGroups
            )
            println("Total: $total")
            total
        }
    }

    var part2Total = part2Futures.sumOf { it.get() }

    println("Part 2 total: $part2Total")
    println("Took ${(System.nanoTime() - part2StartTime) / 1_000_000}ms")
}

fun calculatePossibilities(
    inputStr: CharSequence,
    springGroups: List<Int>,
//    debugPrefix: CharSequence = "",
//    debugGroups: List<Int>
): Long {
    if (inputStr.isEmpty()) {
        return 0
    }
    if (springGroups.isEmpty()) {
        throw IllegalArgumentException("No spring groups")
    }
    //println("inputStr: $inputStr, springGroups: $springGroups")

    val group = springGroups.first()

    var totalPossibilities = 0L
    // Because we cant skip groups the group cant be placed further than the first # in the string
    val firstHashIndex = inputStr.indexOf('#').takeUnless { it == -1 } ?: (inputStr.length - 1)
    // Groups take their own space + 1 for the dot between them
    val minGroupsCharLen = springGroups.sum() + springGroups.size - 1
    val maxPos = firstHashIndex.coerceAtMost(inputStr.length - minGroupsCharLen)

    for (i in 0..maxPos) {
        // Try to place group at index i
        if (canPlaceGroupAtPos(inputStr, group, i)) {
            if (springGroups.size == 1) {
                // If this is the last group, we have found a valid combination
                // Skip if there is a # in the string after the last group
                if (inputStr.drop(i + group).any { it == '#' }) {
                    continue
                }
                //validatePossibility(debugPrefix.toString() + inputStr, debugGroups)
                //println("Option: $debugPrefix$inputStr")
                totalPossibilities++
            } else {
                // If this is not the last group, try to place the remaining groups recursively
                val remainingGroups = springGroups.subList(1, springGroups.size)
                // Remaining input string is string with group and following dot/question mark removed (to prevent group collisions)
//                val newDebugPrefix = debugPrefix.toString() + (inputStr.take(i + group + 1))
                val remainingInputStr = inputStr.drop(i + group + 1)
                totalPossibilities += calculatePossibilities(
                    remainingInputStr,
                    remainingGroups,
//                    newDebugPrefix,
//                    debugGroups
                )
            }
        }
    }

    //println("inputStr: $debugPrefix$inputStr, springGroups: $springGroups, totalPossibilities: $totalPossibilities")
    return totalPossibilities
}

fun validatePossibility(
    possibility: CharSequence,
    springGroups: List<Int>
) {
    val possibilityGroups = mutableListOf<Int>()
    var springGroupLen = 0
    var springGroupIndex = 0
    for (char in possibility) {
        if (char == '#') {
            springGroupLen++
        } else {
            if (springGroupLen > 0) {
                if (springGroups[springGroupIndex] != springGroupLen) {
                    throw IllegalArgumentException("Invalid possibility: $possibility, groups: $possibilityGroups, expected: $springGroups")
                }
                springGroupIndex++
                springGroupLen = 0
            }
        }
    }
    if (springGroupLen > 0 && springGroups[springGroupIndex] != springGroupLen) {
        throw IllegalArgumentException("Invalid possibility: $possibility, groups: $possibilityGroups, expected: $springGroups")
    }
}

/**
 * Tries to place a group of # at position pos in inputStr.
 * Returns null if not possible, otherwise returns the new string with the group placed.
 */
fun canPlaceGroupAtPos(
    inputStr: CharSequence,
    springGroup: Int,
    pos: Int
): Boolean {
    // Not possible if group is larger than remaining string
    if (pos + springGroup > inputStr.length) {
        return false
    }
    // Not possible if group is preceded by #
    for (char in 0..<pos) {
        if (inputStr[char] == '#') {
            return false
        }
    }

    for (i in pos..<(pos + springGroup)) {
        // Not possible if there is a dot in the group
        if (inputStr[i] == '.') {
            return false
        }
    }

    // Check if springGroup is at end of string or is followed by a dot or question mark
    // Not valid if group is followed by #
    return (pos + springGroup == inputStr.length
            || inputStr[pos + springGroup] != '#')
}
