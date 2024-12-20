package year2024.day19

import getInputResourceLines
import java.util.Deque

const val ANSI_ESCAPE_RED = "\u001B[31m"
const val ANSI_ESCAPE_RESET = "\u001B[0m"
const val ASCII_BOX = '█';

const val GRID_SIZE = 71

fun main() {
    val inputRaw = getInputResourceLines(2024, 19)

    val availableTowels = inputRaw[0].split(", ")
    val rootNode = buildTrie(availableTowels)

    val patterns = inputRaw.drop(2)

    val possiblePatterns = patterns.filter {
        val possible = canMakePattern(it.toList(), rootNode)
        println("Pattern: $it, Possible: ${if (possible) "Yes" else "No"}")
        possible
    }

    println("Part 1, possible designs: ${possiblePatterns.size}")
}

fun canMakePattern(pattern: List<Char>, node: TrieNode): Boolean {
    if (pattern.isEmpty()) {
        return true
    }

    val prefixes = findLongestPrefix(node, pattern)
    // Iterate in reverse order to get the longest prefix first
    for (i in prefixes.indices.reversed()) {
        val prefixLength = prefixes[i]
        val remainingPattern = pattern.subList(prefixLength, pattern.size)
        if (canMakePattern(remainingPattern, node)) {
            return true
        }
    }

    return false
}

data class TrieNode(
    val char: Char,
    val children: MutableMap<Char, TrieNode> = mutableMapOf(),
    var isEnd: Boolean = false
)

fun buildTrie(patterns: List<String>): TrieNode {
    val rootNode = TrieNode(' ')

    for (pattern in patterns) {
        var currentNode = rootNode
        for (char in pattern) {
            val childNode = currentNode.children.getOrPut(char) { TrieNode(char) }
            currentNode = childNode
        }
        currentNode.isEnd = true
    }

    return rootNode
}

fun findLongestPrefix(
    rootNode: TrieNode,
    pattern: Collection<Char>,
): MutableList<Int> {
    val prefixLengths = mutableListOf<Int>()
    var prefixLength = 0
    var currentNode = rootNode
    for (char in pattern) {
        val childNode = currentNode.children[char] ?: break
        prefixLength++
        currentNode = childNode
        if (currentNode.isEnd) {
            prefixLengths.add(prefixLength)
        }
    }

    return prefixLengths
}
