package year2024.day6

import getInputResourceLines

enum class Direction {
    UP, RIGHT, DOWN, LEFT
}

data class Position(
    val row: Int,
    val col: Int
) {
    override fun toString(): String {
        return "($row, $col)"
    }
    fun isValid(grid: Array<CharArray>): Boolean {
        return row >= 0 && row < grid.size && col >= 0 && col < grid[0].size
    }
}

fun main() {
    val inputRaw = getInputResourceLines(2024, 6)
    val grid = inputRaw.map { it.toCharArray() }.toTypedArray()

    lateinit var guardPosition: Position
    inputRaw.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->
            if (char == '^') {
                guardPosition = Position(rowIndex, colIndex)
            }
        }
    }

    simulateGuardWalk(grid, guardPosition, inputRaw, true)

    println()
    println("Loop result:")
    println()
    for (row in grid) {
        println(row)
    }

    println()

    val visitedCount = grid.sumOf { row -> row.count { it == '|' || it == '-' || it == '+' } }
    println("Part 1: $visitedCount")

    // Find all unique positions that create a loop
    val part2Grid = inputRaw.map { it.toCharArray() }.toTypedArray()

    var loopPositionCount = 0;
    for (rowIndex in part2Grid.indices) {
        for (colIndex in part2Grid[rowIndex].indices) {
            if (part2Grid[rowIndex][colIndex] == '.') {
                val obstaclePosition = Position(rowIndex, colIndex)
                if (!simulateGuardWalk(part2Grid, guardPosition, inputRaw, false, obstaclePosition)) {
                    println("Loop position: $obstaclePosition")
                    loopPositionCount++
                }
            }
        }
    }

    println("Part 2: $loopPositionCount")
}

/**
 * Returns true if walk ends outside the grid
 * Returns false if walk is a loop
 */
private fun simulateGuardWalk(
    grid: Array<CharArray>,
    initialGuardPosition: Position,
    inputRaw: List<String>,
    writePath: Boolean,
    additionalObstacle: Position? = null
): Boolean {
    var guardPosition = initialGuardPosition
    val guardSteps: MutableSet<Pair<Position, Direction>> = mutableSetOf()
    var guardDirection = Direction.UP

    while (true) {
        val step = guardPosition to guardDirection
        if (guardSteps.contains(step)) {
            // Loop detected
            return false
        }
        guardSteps.add(step)

        if (writePath) {
            grid[guardPosition.row][guardPosition.col] = when (grid[guardPosition.row][guardPosition.col]) {
                '.', '^' -> if (guardDirection == Direction.UP || guardDirection == Direction.DOWN) '|' else '-'
                else -> '+'
            }
        }

        val (nextPosition, newDirection) = determineNextDirectionAndPosition(guardDirection, guardPosition)

        // Pretend value is '#' if it's the additional obstacle to simulate it
        val nextPositionValue = if (nextPosition == additionalObstacle) '#' else inputRaw.safeGet(nextPosition.row, nextPosition.col)
        if (nextPositionValue == '#') {
            guardDirection = newDirection
        } else if (nextPositionValue == null) {
            return true
        } else {
            // Check if putting an obstacle will create a loop
            guardPosition = nextPosition // Mark as visited
        }
    }
}

private fun determineNextDirectionAndPosition(
    guardDirection: Direction,
    guardPosition: Position
) = when (guardDirection) {
    Direction.UP -> Position(guardPosition.row - 1, guardPosition.col) to Direction.RIGHT
    Direction.RIGHT -> Position(guardPosition.row, guardPosition.col + 1) to Direction.DOWN
    Direction.DOWN -> Position(guardPosition.row + 1, guardPosition.col) to Direction.LEFT
    Direction.LEFT -> Position(guardPosition.row, guardPosition.col - 1) to Direction.UP
}

fun List<String>.safeGet(row: Int, col: Int): Char? {
    if (row < 0 || row >= this.size || col < 0 || col >= this[0].length) {
        return null;
    }
    return this[row][col]
}
