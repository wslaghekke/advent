package year2024.day19

import getInputResourceLines
import java.util.*

const val ANSI_ESCAPE_RESET = "\u001B[0m"

fun main() {
    val inputRaw = getInputResourceLines(2024, 19)

    val availableTowels = inputRaw[0].split(", ")
    val rootNode = buildTrie(availableTowels)

    val patterns = inputRaw.drop(2)

    val possiblePatterns = patterns.filter {
        val path = findShortestPath(it, rootNode)
        if (path != null) {
            printPath(path)
        } else {
            println("No path found for $it")
        }
        path != null
    }

    println("Part 1, possible designs: ${possiblePatterns.size}")
}

fun printPath(
    path: Collection<DijkstraNode>,
) {
    // Print the path, with every unique node substring in a different color
    val availableColors = (41..46) + (101..106)
    val colorMap = mutableMapOf<String, Int>()
    path.forEachIndexed { index, node ->
        val substr = node.getSubString()
        val bgColor = colorMap.getOrPut(substr) { availableColors[colorMap.size % availableColors.size] }
        // For color 41..46 we want to use white text, for 101..106 we want to use black text
        val textColor = if (bgColor in 41..46) 30 else 97
        print("\u001B[${bgColor}m\u001B[${textColor}m$substr")
    }
    println(ANSI_ESCAPE_RESET)
}

fun findShortestPath(
    pattern: String,
    rootNode: TrieNode
): List<DijkstraNode>? {
    val cameFrom = mutableMapOf<DijkstraNode, DijkstraNode>()

    val startNode = DijkstraNode(pattern, 0, 0)

    val gScores = mutableMapOf<DijkstraNode, Int>()
    gScores[startNode] = 0

    val fScores = mutableMapOf<DijkstraNode, Int>()
    fScores[startNode] = pattern.length - startNode.endIndex

    val queue = PriorityQueue<DijkstraNode> { a, b ->
        fScores.getOrDefault(a, Int.MAX_VALUE) - fScores.getOrDefault(b, Int.MAX_VALUE)
    }

    queue.add(startNode)

    while (queue.isNotEmpty()) {
        val current = queue.poll()

        if (current.endIndex == pattern.length) {
            return reconstructPath(cameFrom, current)
        }

        for (neighbor in findPrefixes(rootNode, pattern, current.endIndex)) {
            val tentativeGScore = gScores[current]!! + 1
            if (tentativeGScore < gScores.getOrDefault(neighbor, Int.MAX_VALUE)) {
                cameFrom[neighbor] = current
                gScores[neighbor] = tentativeGScore
                fScores[neighbor] = tentativeGScore + pattern.length - neighbor.endIndex
                if (!queue.contains(neighbor)) {
                    queue.add(neighbor)
                }
            }
        }
    }

    return null
}

data class DijkstraNode(
    val pattern: String,
    val startIndex: Int,
    val endIndex: Int,
) {
    fun getSubString() = pattern.substring(startIndex, endIndex)

    override fun toString(): String {
        return "DijkstraNode(startIndex=$startIndex, endIndex=$endIndex, str=${pattern.substring(startIndex, endIndex)})"
    }
}

fun reconstructPath(cameFrom: Map<DijkstraNode, DijkstraNode>, initialCurrent: DijkstraNode): MutableList<DijkstraNode> {
    var current = initialCurrent
    val path = mutableListOf(current)
    while (cameFrom.containsKey(current)) {
        current = cameFrom[current]!!
        path.addFirst(current)
    }
    return path
}

fun findPrefixes(
    rootNode: TrieNode,
    pattern: String,
    startIndex: Int
) = sequence {
    var endIndex = startIndex
    var currentNode = rootNode
    for (i in startIndex..<pattern.length) {
        val char = pattern[i]
        val childNode = currentNode.children[char] ?: break
        endIndex++
        currentNode = childNode
        if (currentNode.isEnd) {
            yield(DijkstraNode(pattern, startIndex, endIndex))
        }
    }
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
