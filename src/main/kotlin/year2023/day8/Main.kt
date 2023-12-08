package year2023.day8

import getInputResourceLines

fun main() {
    val lines = getInputResourceLines(2023, 8)

    val instructions = lines[0].toList()
    val instructionSequence = generateSequence { instructions }.flatten().iterator()

    // Format AAA = (BBB, CCC)
    val parseRegex = Regex("([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)")
    val nodeMap = lines.drop(2).map { line ->
        val (node, left, right) = parseRegex.matchEntire(line)?.destructured ?: throw Exception("Invalid line: $line")
        node to (left to right)
    }.toMap()

    doPart1(instructionSequence, nodeMap)

    var stepCount = 0;
    var currentNodeKeys = nodeMap.keys.filter { it.endsWith("A") }.toTypedArray()

    while (instructionSequence.hasNext()) {
        if (currentNodeKeys.all { it.endsWith("Z") }) {
            println("Reached Z in $stepCount steps")
            break
        }

        when (val instruction = instructionSequence.next()) {
            'L' -> {
                currentNodeKeys.forEachIndexed { index, key ->
                    currentNodeKeys[index] = nodeMap[key]?.first ?: throw Exception("Invalid node: $key")
                }
            }
            'R' -> {
                currentNodeKeys.forEachIndexed { index, key ->
                    currentNodeKeys[index] = nodeMap[key]?.second ?: throw Exception("Invalid node: $key")
                }
            }
            else -> throw Exception("Invalid instruction: $instruction")
        }

        stepCount++
        if (stepCount % 100000 == 0) println("Step $stepCount")
    }
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

        when (val instruction = instructionSequence.next()) {
            'L' -> currentNodeKey = currentNode.first
            'R' -> currentNodeKey = currentNode.second
            else -> throw Exception("Invalid instruction: $instruction")
        }

        stepCount++
    }
}
