package year2024.day10

import getInputResourceLines

data class Position(val row: Int, val col: Int) {
    fun isValid(grid: Array<Array<Int>>) = row in grid.indices && col in grid[0].indices && grid[row][col] != -1
}

const val debugPrint = false

fun main() {
    val inputRaw = getInputResourceLines(2024, 10)

    val grid = inputRaw.map { line -> line.map { it.digitToIntOrNull() ?: -1 }.toTypedArray() }.toTypedArray()

    val trailHeads = grid.flatMapIndexed { row: Int, rowValue: Array<Int> ->
        rowValue.mapIndexedNotNull { col, value -> if(value == 0) Position(row, col) else null }
    }

    val trailHeadRoutes = trailHeads.map { trailHead ->
        getReachablePeaks(grid, trailHead).toList()
    }
    val trailScores = trailHeadRoutes.map { it.toSet().size
    }

    println("Trail score: ${trailScores.sum()}")

    val uniqueTrailScores = trailHeadRoutes.map { it.size }

    println("Unique trail score: ${uniqueTrailScores.sum()}")
}

fun getReachablePeaks(
    grid: Array<Array<Int>>,
    position: Position,
): Sequence<Position> = sequence {
    val positionValue = grid[position.row][position.col];
    if (positionValue == 9) {
        yield(position)
    } else {
        val nextPositions = listOf(
            Position(position.row - 1, position.col),
            Position(position.row + 1, position.col),
            Position(position.row, position.col - 1),
            Position(position.row, position.col + 1),
        ).filter { it.isValid(grid) }

        for (nextPosition in nextPositions) {
            val nextValue = grid[nextPosition.row][nextPosition.col]
            if (nextValue == positionValue + 1) {
                yieldAll(getReachablePeaks(grid, nextPosition))
            }
        }

    }
}

