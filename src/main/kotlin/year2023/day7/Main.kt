package year2023.day7

import getInputResourceLines

fun main() {
    val lines = getInputResourceLines(2023, 7)
    val camelHands = lines.map { line ->
        val (cards, bidStr) = line.split(" ")
        CamelHand(cards, bidStr.toInt())
    }

    val sortedHands = camelHands.sorted()
    val totalWinnings = sortedHands.mapIndexed { index, camelHand ->
        val rank = index + 1;
        val winnings = camelHand.bid * rank
        println(camelHand.toString())
        winnings
    }.sum()

    println("Total winnings: $totalWinnings")
}

class CamelHand(
    val cards: String,
    val bid: Int
) : Comparable<CamelHand> {
    enum class HandType(val value: Int) {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        ONE_PAIR(2),
        HIGH_CARD(1)
    }

    val type: HandType = determineHandType(cards);

    override fun compareTo(other: CamelHand): Int {
        return compareBy<CamelHand> { it.type.value }
            .thenBy { hand -> cardValue(hand.cards[0]) }
            .thenBy { hand -> cardValue(hand.cards[1]) }
            .thenBy { hand -> cardValue(hand.cards[2]) }
            .thenBy { hand -> cardValue(hand.cards[3]) }
            .thenBy { hand -> cardValue(hand.cards[4]) }
            .compare(this, other)
    }

    override fun toString(): String = "$cards (${bid}) - ${type.name}: " + cards.map { cardValue(it) }.joinToString(",")

    companion object {
        fun cardValue(card: Char): Int = card.digitToIntOrNull() ?: when (card) {
            'J' -> 1
            'T' -> 10
            'Q' -> 12
            'K' -> 13
            'A' -> 14
            else -> throw IllegalArgumentException("Unrecognized card '${card}'")
        }

        fun determineHandType(cards: String): HandType {
            val jokerCount = cards.count { it == 'J' }
            if (jokerCount == 5) {
                return HandType.FIVE_OF_A_KIND
            }

            val cardTypeCounts = cards.filter { it != 'J' }.groupingBy { it }.eachCount().values.sortedDescending()

            return when {
                cardTypeCounts[0] + jokerCount == 5 -> HandType.FIVE_OF_A_KIND
                cardTypeCounts[0] + jokerCount == 4 -> HandType.FOUR_OF_A_KIND
                cardTypeCounts[0] + cardTypeCounts[1] + jokerCount == 5 -> HandType.FULL_HOUSE
                cardTypeCounts[0] + jokerCount == 3 -> HandType.THREE_OF_A_KIND
                cardTypeCounts[0] + cardTypeCounts[1] + jokerCount == 4 -> HandType.TWO_PAIR
                cardTypeCounts[0] + jokerCount == 2 -> HandType.ONE_PAIR
                else -> HandType.HIGH_CARD
            }
        }
    }
}
