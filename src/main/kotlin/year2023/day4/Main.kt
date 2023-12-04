package year2023.day4

import getInputResourceLines

fun main() {
    val cards = getInputResourceLines(2023, 4).map { line ->
        val (_, cardContent) = line.split(":")
        val (winningNumbersRaw, haveNumbersRaw) = cardContent.split("|")
        val winningNumbers = winningNumbersRaw.split(" ").mapNotNull { it.trim().toIntOrNull() }.toSet()
        val haveNumbers = haveNumbersRaw.split(" ").mapNotNull { it.trim().toIntOrNull() }
        winningNumbers to haveNumbers
    }

    val winningCardsSum = cards.sumOf { (winningNumbers, haveNumbers) ->
        var totalScore = 0;
        for (haveNumber in haveNumbers) {
            if (haveNumber in winningNumbers) {
                if (totalScore == 0) {
                    totalScore = 1
                } else {
                    totalScore *= 2
                }
            }
        }
        totalScore
    }
    println("Winning card sum: $winningCardsSum")

    val ownedCardCount = cards.map { 1 }.toMutableList()
    cards.forEachIndexed { currIndex, (winningNumbers, haveNumbers) ->
        for (i in 1..haveNumbers.count { winningNumbers.contains(it) }) {
            ownedCardCount[currIndex + i] += ownedCardCount[currIndex]
        }
    }

    println("Resulting total card count: ${ownedCardCount.sum()}")
}
