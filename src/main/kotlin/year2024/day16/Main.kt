package year2024.day16

import getInputResourceLines
import year2024.day15.move
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.time.measureTime

const val ANSI_ESCAPE_RED = "\u001B[31m"
const val ANSI_ESCAPE_RESET = "\u001B[0m"

fun main() {
    val executionTime = measureTime {
        val inputRaw = getInputResourceLines(2024, 16)

        val grid = Array(inputRaw.size) { inputRaw[it].toCharArray() }

        val startPosition = grid.indices
            .flatMap { row -> grid[row].indices.map { col -> row to col } }
            .first { (row, col) -> grid[row][col] == 'S' }

        val reindeerPosition = ReindeerPosition(startPosition.first, startPosition.second)
        grid[reindeerPosition.row][reindeerPosition.col] = '.'

        val targetPosition = grid.indices
            .flatMap { row -> grid[row].indices.map { col -> row to col } }
            .first { (row, col) -> grid[row][col] == 'E' }

        val (score, shortestPath, moves) = findShortestPath(grid, reindeerPosition, targetPosition.first, targetPosition.second)

        printGridWithPath(grid, shortestPath, true)
        println("Shortest path length: $score")

        println("Nodes with same score:")
        printGridWithPath(grid, moves, false)

        val uniqueTiles = moves.map { it.row to it.col }.toSet()

        println("Nodes with same score count: ${uniqueTiles.size}")
    }

    println("Execution time: $executionTime")
}

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST
}

data class ReindeerPosition(
    val row: Int,
    val col: Int,
    val direction: Direction = Direction.EAST
) {
    fun isValid(grid: Array<CharArray>): Boolean {
        return row in grid.indices && col in grid[row].indices && grid[row][col] != '#'
    }

    fun queueScore(targetCol: Int, targetRow: Int): Int {
        return abs(targetCol - col) + abs(targetRow - row)
    }

    fun move(): ReindeerPosition {
        return when (direction) {
            Direction.NORTH -> copy(row = row - 1)
            Direction.EAST -> copy(col = col + 1)
            Direction.SOUTH -> copy(row = row + 1)
            Direction.WEST -> copy(col = col - 1)
        }
    }

    fun moveBack(): ReindeerPosition {
        return when (direction) {
            Direction.NORTH -> copy(row = row + 1)
            Direction.EAST -> copy(col = col - 1)
            Direction.SOUTH -> copy(row = row - 1)
            Direction.WEST -> copy(col = col + 1)
        }
    }

    fun turnLeft(): ReindeerPosition {
        return when (direction) {
            Direction.NORTH -> copy(direction = Direction.WEST)
            Direction.EAST -> copy(direction = Direction.NORTH)
            Direction.SOUTH -> copy(direction = Direction.EAST)
            Direction.WEST -> copy(direction = Direction.SOUTH)
        }
    }

    fun turnRight(): ReindeerPosition {
        return when (direction) {
            Direction.NORTH -> copy(direction = Direction.EAST)
            Direction.EAST -> copy(direction = Direction.SOUTH)
            Direction.SOUTH -> copy(direction = Direction.WEST)
            Direction.WEST -> copy(direction = Direction.NORTH)
        }
    }
}

fun printGridWithPath(grid: Array<CharArray>, path: Collection<ReindeerPosition>, printDirection: Boolean) {
    val copiedGrid = grid.map { it.copyOf() }.toTypedArray()
    for (reindeerPosition in path) {
        copiedGrid[reindeerPosition.row][reindeerPosition.col] = if (printDirection) {
            when (reindeerPosition.direction) {
                Direction.NORTH -> '^'
                Direction.EAST -> '>'
                Direction.SOUTH -> 'v'
                Direction.WEST -> '<'
            }
        } else 'O'
    }

    for (row in copiedGrid) {
        for (char in row) {
            if (char != '.' && char != '#') {
                print(ANSI_ESCAPE_RED)
            }
            print(char)
            print(ANSI_ESCAPE_RESET)
        }
        println()
    }
}


fun findShortestPath(
    grid: Array<CharArray>,
    reindeerPosition: ReindeerPosition,
    targetRow: Int,
    targetCol: Int
): Triple<Int?, List<ReindeerPosition>, Set<ReindeerPosition>> {
    val cameFrom = mutableMapOf<ReindeerPosition, ReindeerPosition>()
    val cameFromMulti = mutableMapOf<ReindeerPosition, MutableSet<ReindeerPosition>>()

    val gScores = mutableMapOf<ReindeerPosition, Int>()
    gScores[reindeerPosition] = 0

    val fScore = mutableMapOf<ReindeerPosition, Int>()
    fScore[reindeerPosition] = reindeerPosition.queueScore(targetCol, targetRow)

    val queue = PriorityQueue<ReindeerPosition> { a, b ->
        fScore.getOrDefault(a, Int.MAX_VALUE) - fScore.getOrDefault(b, Int.MAX_VALUE)
    }
    queue.add(reindeerPosition)

    var bestScore: Int = Int.MAX_VALUE
    var shortestPath: List<ReindeerPosition>? = null

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.row == targetRow && current.col == targetCol) {
            bestScore = gScores[current]!!
            shortestPath = reconstructPath(cameFrom, current)
        }

        for ((neighbor, distance) in possibleMoves(grid, current)) {
            val tentativeGScore = gScores[current]!! + distance
            if (tentativeGScore <= gScores.getOrDefault(neighbor, Int.MAX_VALUE)) {
                cameFrom[neighbor] = current
                cameFromMulti.getOrPut(neighbor) { mutableSetOf() }.add(current)
                gScores[neighbor] = tentativeGScore
                fScore[neighbor] = tentativeGScore + neighbor.queueScore(targetCol, targetRow)
                if (!queue.contains(neighbor) && tentativeGScore <= bestScore) {
                    queue.add(neighbor)
                }
            }
        }
    }

    if (shortestPath != null) {
        return Triple(bestScore, shortestPath, reconstructAllPaths(cameFromMulti, shortestPath.last()))
    }

    throw RuntimeException("No path found")
}

fun reconstructPath(cameFrom: Map<ReindeerPosition, ReindeerPosition>, initialCurrent: ReindeerPosition): MutableList<ReindeerPosition> {
    var current = initialCurrent
    val path = mutableListOf(current)
    while (cameFrom.containsKey(current)) {
        current = cameFrom[current]!!
        path.addFirst(current)
    }
    return path;
}

fun reconstructAllPaths(cameFromMulti: Map<ReindeerPosition, Set<ReindeerPosition>>, initialCurrent: ReindeerPosition): Set<ReindeerPosition> {
    val paths = mutableSetOf(initialCurrent)
    val queue = mutableListOf(initialCurrent)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (cameFromMulti.containsKey(current)) {
            for (neighbor in cameFromMulti[current]!!) {
                queue.add(neighbor)
                paths.add(neighbor)
            }
        }
    }
    return paths
}

fun possibleMoves(grid: Array<CharArray>, reindeerPosition: ReindeerPosition): Sequence<Pair<ReindeerPosition, Int>> = sequence {
    when (reindeerPosition.direction) {
        Direction.NORTH -> {
            val move = reindeerPosition.copy(row = reindeerPosition.row - 1)
            if (move.isValid(grid)) {
                yield(move to 1)
            }
            yield(reindeerPosition.copy(direction = Direction.EAST) to 1000)
            yield(reindeerPosition.copy(direction = Direction.WEST) to 1000)
        }
        Direction.EAST -> {
            val move = reindeerPosition.copy(col = reindeerPosition.col + 1)
            if (move.isValid(grid)) {
                yield(move to 1)
            }
            yield(reindeerPosition.copy(direction = Direction.NORTH) to 1000)
            yield(reindeerPosition.copy(direction = Direction.SOUTH) to 1000)
        }
        Direction.WEST -> {
            val move = reindeerPosition.copy(col = reindeerPosition.col - 1)
            if (move.isValid(grid)) {
                yield(move to 1)
            }
            yield(reindeerPosition.copy(direction = Direction.NORTH) to 1000)
            yield(reindeerPosition.copy(direction = Direction.SOUTH) to 1000)
        }
        Direction.SOUTH -> {
            val move = reindeerPosition.copy(row = reindeerPosition.row + 1)
            if (move.isValid(grid)) {
                yield(move to 1)
            }
            yield(reindeerPosition.copy(direction = Direction.EAST) to 1000)
            yield(reindeerPosition.copy(direction = Direction.WEST) to 1000)
        }
    }
}
