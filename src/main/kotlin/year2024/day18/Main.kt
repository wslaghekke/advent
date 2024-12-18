package year2024.day18

import getInputResourceLines
import java.util.BitSet
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.time.measureTime

const val ANSI_ESCAPE_RED = "\u001B[31m"
const val ANSI_ESCAPE_RESET = "\u001B[0m"
const val ASCII_BOX = '█';

const val GRID_SIZE = 71

fun main() {
    val inputRaw = getInputResourceLines(2024, 18)

    part1(inputRaw)
    part2(inputRaw)
}

private fun part1(inputRaw: List<String>) {
    val executionTime = measureTime {
        val grid = BitSet(GRID_SIZE * GRID_SIZE)

        inputRaw.take(1024).forEach {
            val (col, row) = it.split(",").map { it.toInt() }
            grid[row * GRID_SIZE + col] = true
        }

        printGrid(grid)

        val startPosition = Position(0, 0)
        val endPosition = Position(GRID_SIZE - 1, GRID_SIZE - 1)

        val (score, shortestPath) = findShortestPath(grid, startPosition, endPosition, true)!!

        printGrid(grid, shortestPath)
        println("Shortest path length: $score")
    }
    println("Part 1 execution time: $executionTime")
}

private fun part2(inputRaw: List<String>) {
    val executionTime = measureTime {

        val grid = BitSet(GRID_SIZE * GRID_SIZE)
        val startPosition = Position(0, 0)
        val endPosition = Position(GRID_SIZE - 1, GRID_SIZE - 1)

        val firstByteBlockingExit = inputRaw.find { byte ->
            val (col, row) = byte.split(",").map { it.toInt() }
            grid[row * GRID_SIZE + col] = true

            val path = findShortestPath(grid, startPosition, endPosition, false)

            path === null
        }
        println("First byte that would block exit: $firstByteBlockingExit")
    }
    println("Part 2 execution time: $executionTime")
}

@JvmInline
value class Position(val gridIndex: Int) {
    constructor(row: Int, col: Int) : this(row * GRID_SIZE + col)

    val row: Int inline get() = gridIndex / GRID_SIZE
    val col: Int inline get() = gridIndex % GRID_SIZE

    fun distance(other: Position): Int {
        return abs(row - other.row) + abs(col - other.col)
    }

    override fun toString(): String {
        return "($row, $col)"
    }
}

fun printGrid(grid: BitSet, shortestPath: Collection<Position>? = null, overwrite: Boolean = false) {
    // If overwrite is set we want to move the cursor up GRID_SIZE lines to overwrite the previous grid
    // Also make sure we move the cursor to the start of the line
    if (overwrite) {
        print("\u001b[${GRID_SIZE}A\u001b[0G")
    }
    for (row in 0..<GRID_SIZE) {
        for (col in 0..<GRID_SIZE) {
            print(
                if (shortestPath?.contains(Position(row, col)) == true) {
                    ANSI_ESCAPE_RED + "O" + ANSI_ESCAPE_RESET
                } else if (grid[row * GRID_SIZE + col]) {
                    ASCII_BOX
                } else {
                    "."
                }
            )
        }
        println()
    }
    println()
}

fun findShortestPath(
    grid: BitSet,
    start: Position,
    goal: Position,
    keepPath: Boolean
): Pair<Int, List<Position>>? {
    val cameFrom = mutableMapOf<Position, Position>()

    val gScores = IntArray(GRID_SIZE * GRID_SIZE) { Int.MAX_VALUE }
    gScores[start.gridIndex] = 0

    val fScores = IntArray(GRID_SIZE * GRID_SIZE) { Int.MAX_VALUE }
    fScores[start.gridIndex] = start.distance(goal)

    val queue = PriorityQueue<Position> { a, b ->
        fScores[a.gridIndex] - fScores[b.gridIndex]
    }

    queue.add(start)

    val neighbors = arrayOfNulls<Int>(4)

    var visitCount = 0

    while (queue.isNotEmpty()) {
        val current = queue.poll()

        visitCount++
        if (current == goal) {
            val bestScore = gScores[current.gridIndex]
            val shortestPath = if (keepPath) reconstructPath(cameFrom, current) else emptyList()
            //println("Visited $visitCount nodes to find the shortest path")
            return bestScore to shortestPath
        }

        neighbors[0] = if (current.col + 1 < GRID_SIZE) current.gridIndex + 1 else null
        neighbors[1] = if (current.row + 1 < GRID_SIZE) current.gridIndex + GRID_SIZE else null
        neighbors[2] = if (current.col - 1 >= 0) current.gridIndex - 1 else null
        neighbors[3] = if (current.row - 1 >= 0) current.gridIndex - GRID_SIZE else null

        for (neighborGridIndex in neighbors) {
            if (neighborGridIndex == null || grid[neighborGridIndex]) {
                // Skip if the neighbor is out of bounds or a wall
                continue
            }

            val tentativeGScore = gScores[current.gridIndex] + 1
            if (tentativeGScore < gScores[neighborGridIndex]) {
                val neighbor = Position(neighborGridIndex)
                gScores[neighborGridIndex] = tentativeGScore
                fScores[neighborGridIndex] = tentativeGScore + neighbor.distance(goal)
                if (keepPath) cameFrom[neighbor] = current
                if (!queue.contains(neighbor)) {
                    queue.add(neighbor)
                }
            }
        }
    }

    return null
}

fun reconstructPath(cameFrom: Map<Position, Position>, initialCurrent: Position): MutableList<Position> {
    var current = initialCurrent
    val path = mutableListOf(current)
    while (cameFrom.containsKey(current)) {
        current = cameFrom[current]!!
        path.addFirst(current)
    }
    return path;
}
