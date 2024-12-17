package year2024.day17

import getInputResourceLines
import kotlin.math.pow
import kotlin.time.measureTime

const val ANSI_ESCAPE_RED = "\u001B[31m"
const val ANSI_ESCAPE_RESET = "\u001B[0m"

fun main() {
    val executionTime = measureTime {
        val inputRaw = getInputResourceLines(2024, 17)

        val registerA = inputRaw[0].substringAfter("Register A: ").toInt()
        val registerB = inputRaw[1].substringAfter("Register B: ").toInt()
        val registerC = inputRaw[2].substringAfter("Register C: ").toInt()
        val instructions = inputRaw[4].substringAfter("Program: ").split(",").map { it.toInt() }

        val computer = Computer(registerA, registerB, registerC, instructions)

        val output = computer.executeInstruction().toList()

        println("The output is: ${output.joinToString(",")}")
    }

    println("Execution time: $executionTime")
}

class Computer(
    private var registerA: Int = 0,
    private var registerB: Int = 0,
    private var registerC: Int = 0,
    private val instructions: List<Int>
) {
    private var instructionPointer = 0

    fun executeInstruction(): Sequence<Int> = sequence {
        while (instructionPointer < instructions.size) {
            val opCode = instructions[instructionPointer]
            val operand = instructions[instructionPointer + 1]
            when (opCode) {
                0 -> {
                    // adv - Division to registerA
                    registerA = (registerA / 2.0.pow(getOpComboValue(operand).toDouble())).toInt()
                }
                1 -> {
                    // bxl - bitwise xor operand
                    registerB = registerB xor operand
                }
                2 -> {
                    // bst - combo modulo 8
                    registerB = getOpComboValue(operand) % 8
                }
                3 -> {
                    // jnz - jump if not zero
                    if (registerA != 0) {
                        instructionPointer = operand
                        // Continue to avoid incrementing the instructionPointer at the end of the function
                        continue
                    }
                }
                4 -> {
                    // bxc - bitwise xor registerC
                    registerB = registerB xor registerC
                }
                5 -> {
                    // out - output combo modulo 8
                    yield(getOpComboValue(operand) % 8)
                }
                6 -> {
                    // bdv - Division to registerB
                    registerB = (registerA / 2.0.pow(getOpComboValue(operand).toDouble())).toInt()
                }
                7 -> {
                    // cdv - Division to registerC
                    registerC = (registerA / 2.0.pow(getOpComboValue(operand).toDouble())).toInt()
                }
            }
            instructionPointer += 2
        }
    }

    private fun getOpComboValue(opCombo: Int): Int {
        return when (opCombo) {
            0,1,2,3 -> opCombo
            4 -> registerA
            5 -> registerB
            6 -> registerC
            else -> throw IllegalArgumentException("Invalid comboOp: $opCombo")
        }
    }
}
