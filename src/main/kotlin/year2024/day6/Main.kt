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

    val guardSteps: MutableSet<Pair<Position, Direction>> = mutableSetOf()
    val potentialObstaclePositions: MutableSet<Position> = mutableSetOf()
    var guardDirection = Direction.UP

    while (true) {
        grid[guardPosition.row][guardPosition.col] = when (grid[guardPosition.row][guardPosition.col]) {
            '.', '^' -> if (guardDirection == Direction.UP || guardDirection == Direction.DOWN) '|' else '-'
            else -> '+'
        }
        guardSteps.add(guardPosition to guardDirection)

        val (nextPosition, newDirection) = determineNextDirectionAndPosition(guardDirection, guardPosition)

        val nextPositionValue = inputRaw.safeGet(nextPosition.row, nextPosition.col)
        if (nextPositionValue == '#') {
            guardDirection = newDirection
        } else if (nextPositionValue == null) {
            break // Out of bounds
        } else {
            // Check if putting an obstacle will create a loop
            addPotentialObstacleLoop(
                guardPosition,
                guardDirection,
                grid,
                guardSteps,
                potentialObstaclePositions,
                nextPosition
            )

            guardPosition = nextPosition // Mark as visited
        }
    }

    println()
    println("Loop result:")
    println()
    for (row in grid) {
        println(row)
    }

    println()

    val visitedCount = grid.sumOf { row -> row.count { it == '|' || it == '-' || it == '+' } }
    println("Part 1: $visitedCount")

    println("Part 2: ${potentialObstaclePositions.size}")
}

fun addPotentialObstacleLoop(
    guardPosition: Position,
    guardDirection: Direction,
    grid: Array<CharArray>,
    guardSteps: MutableSet<Pair<Position, Direction>>,
    potentialObstaclePositions: MutableSet<Position>,
    potentialObstablePosition: Position
) {
    var position = guardPosition
    val (_, direction) = determineNextDirectionAndPosition(guardDirection, guardPosition)

    while (position.isValid(grid)) {
        val (nextPosition, _) = determineNextDirectionAndPosition(direction, position)

        if (guardSteps.contains(nextPosition to direction)) {
            if (potentialObstaclePositions.add(potentialObstablePosition)) {
                //printObstacleFound(obstaclePosition, grid)
            }

            return
        }

        position = nextPosition
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

fun printObstacleFound(obstaclePosition: Position, grid: Array<CharArray>) {
    println("Potential loop detected, add obstacle $obstaclePosition")
    for (row in grid.indices) {
        for (col in grid[row].indices) {
            if (row == obstaclePosition.row && col == obstaclePosition.col) {
                print('O')
            } else {
                print(grid[row][col])
            }
        }
        println()
    }
}

fun List<String>.safeGet(row: Int, col: Int): Char? {
    if (row < 0 || row >= this.size || col < 0 || col >= this[0].length) {
        return null;
    }
    return this[row][col]
}
