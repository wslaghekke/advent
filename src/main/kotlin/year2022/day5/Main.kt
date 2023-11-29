package year2022.day5

import getInputResourceLines
import java.util.Deque
import java.util.LinkedList

fun main(args: Array<String>) {
    val inputRaw = getInputResourceLines(2022, 5)
    val (stacks, instructions) = parseCraneInput(inputRaw)

    printStacks(stacks)
    for (instruction in instructions) {
        applyInstruction(stacks, instruction)
        printStacks(stacks)
    }

    val topCrates = stacks.map { stack -> stack.last() }.joinToString("")

    println("Top crates 9000: $topCrates")

    val (stacks2, instructions2) = parseCraneInput(inputRaw)

    printStacks(stacks2)
    for (instruction in instructions2) {
        applyInstruction9001(stacks2, instruction)
        printStacks(stacks2)
    }

    val topCrates2 = stacks2.map { stack -> stack.last() }.joinToString("")

    println("Top crates 9001: $topCrates2")
}

fun applyInstruction(stacks: MutableList<Deque<Char>>, instruction: Instruction) {
    repeat(instruction.moveCount) {
        val from = stacks[instruction.fromPos - 1]
        val to = stacks[instruction.toPos - 1]

        val movedItem = from.removeLast()
        if (movedItem !== null) {
            to.addLast(movedItem)
        } else {
            println("Failed to move crate, stack was empty")
        }
    }
}

fun applyInstruction9001(stacks: MutableList<Deque<Char>>, instruction: Instruction) {
    val from = stacks[instruction.fromPos - 1]
    val to = stacks[instruction.toPos - 1]

    val movedStack = (1..instruction.moveCount).map { from.removeLast() }.reversed()

    to.addAll(movedStack)
}

fun printStacks(stacks: MutableList<Deque<Char>>) {
    stacks.forEachIndexed { index, chars ->
        println("$index ${chars.joinToString(" ")}")
    }
    println()
}

fun parseCraneInput(inputLines: List<String>): Pair<MutableList<Deque<Char>>, MutableList<Instruction>> {
    val stacks = mutableListOf<Deque<Char>>()
    val instructions = mutableListOf<Instruction>()

    for (line in inputLines) {
        // Skip empty lines
        if (line.isBlank()) continue

        // If line starts with "m" then it's an instruction
        // If line starts with " " or "[" it's a crate or stack nr line
        if (line[0] == 'm') {
            instructions.add(parseInstruction(line))
        } else if (line[0] == ' ' || line[0] == '[') {
            // Ignore stack label line
            if (line[1] == '1') continue

            line.chunked(4).forEachIndexed { index, box ->
                val stack: Deque<Char> = stacks.getOrNull(index) ?: run {
                    val list = LinkedList<Char>()
                    stacks.add(list)
                    list
                }

                val boxContent = box[1]
                if (boxContent != ' ') {
                    // Add to start of stack because we fill the stack top down
                    stack.addFirst(boxContent)
                }
            }
        }
    }

    return stacks to instructions
}

val instructionRegex = Regex("move (\\d+) from (\\d+) to (\\d+)")
fun parseInstruction(line: String): Instruction {
    return instructionRegex.find(line)?.destructured?.let { (moveCount, fromPos, toPos) ->
        Instruction(moveCount.toInt(), fromPos.toInt(), toPos.toInt())
    } ?: throw RuntimeException("Invalid instruction line: '$line'")
}

data class Instruction(
    val moveCount: Int,
    val fromPos: Int,
    val toPos: Int
)
