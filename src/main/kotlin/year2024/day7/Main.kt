package year2024.day7

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2024, 7)

    val equations = inputRaw.map { line ->
        val (expectedResult, numbers) = line.split(": ")
        val numbersList = numbers.split(" ").map { it.toLong() }
        Equation(expectedResult.toLong(), numbersList)
    }


    val validEquations = equations.filter { equation ->
        val valid = equation.solve(false)
        println("Equation: $equation, valid: $valid")
        valid
    }.sumOf { it.expectedResult }

    println("Sum of valid equations: $validEquations")

    val validEquationsWithConcat = equations.filter { equation ->
        val valid = equation.solve(true)
        println("Equation: $equation, valid: $valid")
        valid
    }.sumOf { it.expectedResult }

    println("Sum of valid equations with concat: $validEquationsWithConcat")
}

data class Equation(
    val expectedResult: Long,
    val numbers: List<Long>
) {
    override fun toString(): String {
        return "Equation(expectedResult=$expectedResult, numbers=$numbers)"
    }
    fun solve(
        enableConcatOperator: Boolean
    ): Boolean {
        // Determine if we can get the expected result by adding or multiplying the numbers
        // Numbers have to be used in the order they appear
        println("Solving equation: $this")
        return potentialSolutionSequence(
            Calculation(numbers[0]),
            numbers.slice(1 until numbers.size),
            enableConcatOperator
        ).any { calculation ->
            val result = calculation.calculateLeftToRightResult()
            println("Result for $calculation: $result")
            result == expectedResult
        }
    }

    private fun potentialSolutionSequence(
        calculation: Calculation,
        remainingNumbers: List<Long>,
        enableConcatOperator: Boolean
    ): Sequence<Calculation> = sequence {
        val firstNumber = remainingNumbers[0]

        val multiply = calculation.addMultiplication().addNumber(firstNumber)
        val addition = calculation.addAddition().addNumber(firstNumber)
        val concat = calculation.addConcat().addNumber(firstNumber)

        if (remainingNumbers.size == 1) {
            yield(multiply)
            yield(addition)
            if (enableConcatOperator) {
                yield(concat)
            }
        } else {
            yieldAll(potentialSolutionSequence(
                multiply,
                remainingNumbers.slice(1 until remainingNumbers.size),
                enableConcatOperator
            ))
            yieldAll(potentialSolutionSequence(
                addition,
                remainingNumbers.slice(1 until remainingNumbers.size),
                enableConcatOperator
            ))
            if (enableConcatOperator) {
                yieldAll(potentialSolutionSequence(
                    concat,
                    remainingNumbers.slice(1 until remainingNumbers.size),
                    enableConcatOperator
                ))
            }
        }
    }
}

class Calculation private constructor(
    private val parts: List<CalculationPart>
) {
    constructor(initialNumber: Long) : this(listOf(Number(initialNumber)))

    sealed interface CalculationPart
    data class Number(val value: Long) : CalculationPart {
        override fun toString(): String = value.toString()
    }
    data object MultiplyOperator : CalculationPart {
        override fun toString(): String = "*"
    }
    data object AdditionOperator : CalculationPart {
        override fun toString(): String = "+"
    }
    data object ConcatOperator : CalculationPart {
        override fun toString(): String = "||"
    }

    fun addNumber(number: Long): Calculation {
        return Calculation(parts + Number(number))
    }

    fun addMultiplication(): Calculation {
        return Calculation(parts + MultiplyOperator)
    }

    fun addAddition(): Calculation {
        return Calculation(parts + AdditionOperator)
    }

    fun addConcat(): Calculation {
        return Calculation(parts + ConcatOperator)
    }

    override fun toString(): String {
        return parts.joinToString(" ")
    }

    fun calculateLeftToRightResult(): Long {
        var lastPart: CalculationPart? = null;
        var subTotal = 0L;
        for (part in parts) {
            if (part is Number) {
                when(lastPart) {
                    null -> subTotal = part.value
                    is MultiplyOperator -> subTotal *= part.value
                    is AdditionOperator -> subTotal += part.value
                    is ConcatOperator -> subTotal = (subTotal.toString() + part.value.toString()).toLong()
                    else -> throw IllegalStateException("Unexpected part: $lastPart")
                }
            }
            lastPart = part
        }

        return subTotal
    }

//    fun calculateResult(): Long {
//        // Calculate result in two steps: first multiplication, then addition
//        var subTotal = 1L;
//        val newPartList = mutableListOf<Long>()
//
//        println("Calculating result for $this")
//
//        for (part in parts) {
//            when (part) {
//                is Number -> subTotal *= part.value
//                is AdditionOperator -> {
//                    // Reset subTotal
//                    newPartList.add(subTotal)
//                    subTotal = 1
//                }
//                is MultiplyOperator -> Unit
//            }
//        }
//        newPartList.add(subTotal)
//
//        println("After multiplication: $newPartList")
//
//        return newPartList.sum()
//    }
}
