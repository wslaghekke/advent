package year2023.day8

import getInputResourceLines
import utils.lcm

fun main() {
    val lines = getInputResourceLines(2023, 8)

    val instructions = lines[0].toList()
    val instructionSequence = generateSequence(instructions) { instructions }.flatten()

    // Format AAA = (BBB, CCC)
    val parseRegex = Regex("([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)")
    val nodeMap = lines.drop(2).map { line ->
        val (node, left, right) = parseRegex.matchEntire(line)?.destructured ?: throw Exception("Invalid line: $line")
        node to (left to right)
    }.toMap()

    doPart1(instructionSequence.iterator(), nodeMap)

    val currentNodeKeys = nodeMap.keys.filter { it.endsWith("A") }.toTypedArray()

    val loopLengths = currentNodeKeys.map { initialKey ->
        val instructionIterator = instructionSequence.iterator()
        var stepCount = 0
        var currentKey = initialKey
        while (instructionIterator.hasNext()) {
            if (currentKey.endsWith('Z')) {
                println("Reached Z in $stepCount steps ($initialKey)")
                break
            }

            currentKey = when (val instruction = instructionIterator.next()) {
                'L' -> nodeMap[currentKey]?.first!!
                'R' -> nodeMap[currentKey]?.second!!
                else -> throw Exception("Invalid instruction: $instruction")
            }

            stepCount++
        }
        stepCount
    }

    println("Steps till synchronized Z: ${loopLengths.lcm()}")
}

private fun doPart1(
    instructionSequence: Iterator<Char>,
    nodeMap: Map<String, Pair<String, String>>
) {
    var stepCount = 0;
    var currentNodeKey = "AAA"

    while (instructionSequence.hasNext()) {
        if (currentNodeKey == "ZZZ") {
            println("Reached ZZZ in $stepCount steps")
            break
        }
        val currentNode = nodeMap[currentNodeKey] ?: throw Exception("Invalid node: $currentNodeKey")

        currentNodeKey = when (val instruction = instructionSequence.next()) {
            'L' -> currentNode.first
            'R' -> currentNode.second
            else -> throw Exception("Invalid instruction: $instruction")
        }

        stepCount++
    }
}
