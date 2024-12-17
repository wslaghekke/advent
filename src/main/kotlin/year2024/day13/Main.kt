package year2024.day13

import getInputResourceLines
import kotlin.time.measureTime

fun main() {
    val executionTime = measureTime {

        val inputRaw = getInputResourceLines(2024, 13).joinToString("\n")

        val clawMachineRegex =
            Regex("Button A: X\\+(\\d+), Y\\+(\\d+)\nButton B: X\\+(\\d+), Y\\+(\\d+)\nPrize: X=(\\d+), Y=(\\d+)")

        val clawMachines = clawMachineRegex.findAll(inputRaw).map {
            val (buttonAX, buttonAY, buttonBX, buttonBY, prizeX, prizeY) = it.destructured
            ClawMachine(
                buttonAX.toLong(),
                buttonAY.toLong(),
                buttonBX.toLong(),
                buttonBY.toLong(),
                prizeX.toLong(),
                prizeY.toLong()
            )
        }

        val allPossiblePrizesMinTokens = clawMachines.mapNotNull { it.findPrizeMinTokens() }

        println("The minimum number of tokens needed to win all prizes is ${allPossiblePrizesMinTokens.sum()}")

        // Part 2, prizes are actually 10000000000000 higher in X and Y
        val clawMachinesPart2 = clawMachines.map {
            ClawMachine(
                it.buttonAX,
                it.buttonAY,
                it.buttonBX,
                it.buttonBY,
                it.prizeX + 10_000_000_000_000,
                it.prizeY + 10_000_000_000_000
            )
        }

        val allPossiblePrizesMinTokensPart2 = clawMachinesPart2.mapNotNull { it.findPrizeMinTokens() }

        println("The minimum number of tokens needed to win all prizes in part 2 is ${allPossiblePrizesMinTokensPart2.sum()}")
    }

    println("Execution time: $executionTime")
}

data class ClawMachine(
    val buttonAX: Long,
    val buttonAY: Long,
    val buttonBX: Long,
    val buttonBY: Long,
    val prizeX: Long,
    val prizeY: Long
) {
    fun findPrizeMinTokens(): Long? {
        var totalA = 0L
        var totalB = 0L

        val aClicksXMultiplier = buttonAX * buttonBY
        val aClicksYMultiplier = buttonAY * buttonBX * -1

        val prizeXMultiplied = prizeX * buttonBY
        val prizeYMultiplied = prizeY * buttonBX * -1

        val aClicksMultiplierCombined = aClicksXMultiplier + aClicksYMultiplier
        val prizeMultipliedCombined = prizeXMultiplied + prizeYMultiplied

        val aClicks = prizeMultipliedCombined / aClicksMultiplierCombined
        val aClicksRemainder = prizeMultipliedCombined % aClicksMultiplierCombined

        if (aClicksRemainder == 0L) {
            val bClicks = (prizeX - buttonAX * aClicks) / buttonBX
            val bClicksRemainder = (prizeX - buttonAX * aClicks) % buttonBX

            if (bClicksRemainder == 0L) {
                totalA = aClicks
                totalB = bClicks
            }
        }

        return if (totalA == 0L && totalB == 0L) {
            null
        } else {
            (totalA * 3) + totalB
        }
    }
}
