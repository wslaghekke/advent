package year2024.day15

import getInputResourceLines
import kotlin.time.measureTime

const val ANSI_ESCAPE_RED = "\u001B[31m"
const val ANSI_ESCAPE_RESET = "\u001B[0m"

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class Position(val row: Int, val col: Int) {
    fun isValid(grid: Array<Array<Char>>): Boolean {
        return row in grid.indices && col in grid[row].indices
    }
}

const val DEBUG_PRINT = false

fun main() {
    val executionTime = measureTime {
        val inputRaw = getInputResourceLines(2024, 15)

        val mazeLines = inputRaw.takeWhile { it.isNotBlank() }
        val instructions = inputRaw.dropWhile { it.isNotBlank() }.drop(1).joinToString("")

        part1(mazeLines, instructions)
        part2(mazeLines, instructions)
    }

    println("Execution time: $executionTime")
}

private fun part1(mazeLines: List<String>, instructions: String) {
    var robotPosition: Position? = null
    val grid = Array(mazeLines.size) { row ->
        Array(mazeLines[row].length) { col ->
            if (mazeLines[row][col] == '@') {
                robotPosition = Position(row, col)
            }

            mazeLines[row][col]
        }
    }

    println("Initial state:")
    printGrid(grid)

    for (instruction in instructions) {
        val direction = when (instruction) {
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            else -> throw IllegalStateException("Unknown instruction $instruction")
        }

        robotPosition = move(grid, robotPosition!!, direction) ?: robotPosition
        if (DEBUG_PRINT) {
            println("Move $instruction:")
            printGrid(grid)
        }
    }

    println("Final state:")
    printGrid(grid)

    val boxGpsCoordinatesSum = grid.flatMapIndexed { row: Int, rowData: Array<Char> ->
        rowData.mapIndexed { col: Int, item: Char ->
            if (item == 'O') {
                (row * 100) + col
            } else {
                0
            }
        }
    }

    println("Box GPS coordinates sum: ${boxGpsCoordinatesSum.sum()}")
}

private fun part2(mazeLines: List<String>, instructions: String) {
    var expandedRobotPosition: Position? = null
    val expandedGrid = Array(mazeLines.size) { row ->
        Array(mazeLines[row].length * 2) { col ->

            when (val item = mazeLines[row][col / 2]) {
                'O' -> if (col % 2 == 0) '[' else ']' // Wide box
                '@' -> if (col % 2 == 0) {
                    expandedRobotPosition = Position(row, col)
                    '@'
                } else '.' // Robot
                '.' -> '.' // Empty space
                '#' -> '#' // Wall
                else -> item
            }
        }
    }

    println("Expanded grid:")
    printGrid(expandedGrid)

    var i = 0;
    for (instruction in instructions) {
        val direction = when (instruction) {
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            else -> throw IllegalStateException("Unknown instruction $instruction")
        }

        val moves = calculateMoves(expandedGrid, expandedRobotPosition!!, direction)
        if (moves != null) {
            // Robot should be the last move
            expandedRobotPosition = moves.last().second

            executeMoves(expandedGrid, moves)
        }

        if (expandedGrid[expandedRobotPosition!!] != '@') {
            throw IllegalStateException("Robot should be at the new position")
        }
        if (DEBUG_PRINT) {
            println("Move $instruction, score after $i moves: ${calculateScore(expandedGrid)}")
            printGrid(expandedGrid)
            i++
        }
    }

    println()
    println("Final expanded grid:")
    printGrid(expandedGrid)

    val expandedBoxCoordinatesSum = expandedGrid.flatMapIndexed { row: Int, rowData: Array<Char> ->
        rowData.mapIndexed { col: Int, item: Char ->
            if (item == '[') {
                (row * 100) + col
            } else {
                0
            }
        }
    }

    println("Expanded box GPS coordinates sum: ${expandedBoxCoordinatesSum.sum()}")
}

fun calculateScore(grid: Array<Array<Char>>): Int {
    return grid.flatMapIndexed { row: Int, rowData: Array<Char> ->
        rowData.mapIndexed { col: Int, item: Char ->
            if (item == '[') {
                (row * 100) + col
            } else {
                0
            }
        }
    }.sum()
}

fun move(grid: Array<Array<Char>>, position: Position, direction: Direction): Position? {
    val item = grid[position]
    if (item == '#') {
        // Wall cant move
        return null
    }
    if (item == '.') {
        // Empty space
        throw IllegalStateException("Empty space should not be moved")
    }

    val newPosition = newPosition(grid, position, direction) ?: return null
    when (val newItem = grid[newPosition.row][newPosition.col]) {
        '#' -> {
            // Wall
            return null
        }
        '.' -> {
            // Empty space
            grid[position] = '.'
            grid[newPosition] = item
            return newPosition
        }
        'O' -> {
            // Box
            val boxMoved = move(grid, newPosition, direction)
            if (boxMoved != null) {
                grid[position] = '.'
                grid[newPosition] = item
                return newPosition
            }
            return null
        }
        else -> {
            throw IllegalStateException("Unknown item $newItem")
        }
    }
}

/**
 * This function returns a set of moves resulting from moving the item at position in direction
 * if the move is not possible null is returned
 */
fun calculateMoves(grid: Array<Array<Char>>, position: Position, direction: Direction): Set<Pair<Position, Position>>? {
    val positionItem = grid[position]
    if (positionItem == '#') {
        // Wall cant move
        return null
    }
    if (positionItem == '.') {
        // Empty space doesnt need moves
        return emptySet()
    }

    val newPosition = newPosition(grid, position, direction) ?: return null

    val moves = mutableSetOf<Pair<Position, Position>>()
    if (
        // Special case for wide box moving up or down
        (direction == Direction.UP || direction == Direction.DOWN) && (positionItem == '[' || positionItem == ']')
    ) {
        val boxPositionLeft = if (positionItem == '[') position else Position(position.row, position.col - 1)
        val boxPositionRight = if (positionItem == ']') position else Position(position.row, position.col + 1)

        val newPositionLeft = if (positionItem == '[') newPosition else Position(newPosition.row, newPosition.col - 1)
        val newPositionRight = if (positionItem == ']') newPosition else Position(newPosition.row, newPosition.col + 1)

        val leftMove = calculateMoves(grid, newPositionLeft, direction) ?: return null
        moves.addAll(leftMove)

        // If left position is left part of box we don't need to calculate right moves because they will be the same
        if (grid[newPositionLeft] != '[') {
            val rightMove = calculateMoves(grid, newPositionRight, direction) ?: return null
            moves.addAll(rightMove)
        }

        moves.add(boxPositionLeft to newPositionLeft)
        moves.add(boxPositionRight to newPositionRight)
    } else {
        // Simple case
        when (grid[newPosition]) {
            '#' -> {
                // Wall
                return null
            }
            '.' -> {
                // Empty space
                moves.add(position to newPosition)
            }
            '[', ']' -> {
                val boxMoves = calculateMoves(grid, newPosition, direction)
                if (boxMoves != null) {
                    moves.addAll(boxMoves)
                    moves.add(position to newPosition)
                } else {
                    return null
                }
            }
        }
    }

    return moves
}

fun executeMoves(grid: Array<Array<Char>>, moves: Collection<Pair<Position, Position>>) {
    for ((from, to) in moves) {
        val item = grid[from]
        grid[from] = '.'
        grid[to] = item
    }
}


operator inline fun Array<Array<Char>>.get(position: Position): Char {
    return this[position.row][position.col]
}

operator inline fun <T> Array<Array<T>>.set(position: Position, value: T) {
    this[position.row][position.col] = value
}

fun newPosition(grid: Array<Array<Char>>, position: Position, direction: Direction): Position? {
    val newPosition = when (direction) {
        Direction.UP -> Position(position.row - 1, position.col)
        Direction.DOWN -> Position(position.row + 1, position.col)
        Direction.LEFT -> Position(position.row, position.col - 1)
        Direction.RIGHT -> Position(position.row, position.col + 1)
    }
    return if (newPosition.isValid(grid)) {
        newPosition
    } else {
        null
    }
}

fun printGrid(grid: Array<Array<Char>>) {
    for (y in grid.indices) {
        for (x in grid[y].indices) {
            if (grid[y][x] == '@') {
                print(ANSI_ESCAPE_RED + grid[y][x] + ANSI_ESCAPE_RESET)
            } else {
                print(grid[y][x])
            }
        }
        println()
    }
}
