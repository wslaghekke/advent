package year2024.day14

import getInputResourceLines

const val GRID_WIDTH = 101
const val GRID_HEIGHT = 103

const val FULL_BLOCK_CHAR = '█';
const val TOP_HALF_BLOCK_CHAR = '▀';
const val BOTTOM_HALF_BLOCK_CHAR = '▄';

// Everything after this is in red
const val ANSI_RED = "\u001B[31m"
// Resets previous color codes
const val ANSI_RESET = "\u001B[0m"

data class Robot(
    var row: Int,
    var col: Int,
    val rowVelocity: Int,
    val colVelocity: Int
) {
    fun simulateStep(times: Int) {
        val rowDelta = times * rowVelocity % GRID_HEIGHT
        val colDelta = times * colVelocity % GRID_WIDTH

        var newRow = (row + rowDelta) % GRID_HEIGHT
        if (newRow < 0) {
            newRow += GRID_HEIGHT
        }

        var newCol = (col + colDelta) % GRID_WIDTH
        if (newCol < 0) {
            newCol += GRID_WIDTH
        }

        row = newRow
        col = newCol
    }
}

fun main() {
    val inputRaw = getInputResourceLines(2024, 14)

    val robotRegex = Regex("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)")
    val robots = inputRaw.map {
        val match = robotRegex.matchEntire(it)
        val (col, row, colVelocity, rowVelocity) = match!!.destructured
        Robot(row.toInt(), col.toInt(), rowVelocity.toInt(), colVelocity.toInt())
    }

    printGrid(robots)

    val after100Seconds = robots.onEach { it.copy().simulateStep(100) }

    printGrid(after100Seconds)

    val robotsPerQuadrant = countRobotsPerQuadrant(after100Seconds)
    println("Robots per quadrant safety factor: $robotsPerQuadrant")

    var i = 0L
    do {
        for (robot in robots) {
            robot.simulateStep(1)
        }
    } while (!isTree(robots, i++ % 1000 == 0L))

    println("Iterations until tree: $i")
}

fun countRobotsPerQuadrant(robotPositions: List<Robot>): Int {
    val rowPivot = GRID_HEIGHT / 2
    val colPivot = GRID_WIDTH / 2

    var topLeft = 0;
    var topRight = 0;
    var bottomLeft = 0;
    var bottomRight = 0;
    for(position in robotPositions) {
        if (position.row < rowPivot) {
            if (position.col < colPivot) {
                topLeft++
            } else if(position.col > colPivot) {
                topRight++
            }
        } else if (position.row > rowPivot) {
            if (position.col < colPivot) {
                bottomLeft++
            } else if(position.col > colPivot) {
                bottomRight++
            }
        }
    }

    return topLeft * topRight * bottomLeft * bottomRight;
}

fun printGrid(robots: List<Robot>) {
    val grid = Array(GRID_HEIGHT) { Array(GRID_WIDTH) { 0 } }
    robots.forEach {
        grid[it.row][it.col]++;
    }

    for (row in grid) {
        for (cell in row) {
            print(if (cell > 0) cell else ".")
        }
        println()
    }
    println()
}

fun isTree(robots: List<Robot>, print: Boolean): Boolean {
    val grid = Array(GRID_HEIGHT) { Array(GRID_WIDTH) { false } }
    robots.forEach {
        grid[it.row][it.col] = true;
    }

    // We have a tree if
    // A specific cell is true
    // The cell below-right below and below-left are true

    var treeTop: Pair<Int, Int>? = null
    tree@ for (row in grid.indices) {
        for (col in grid[0].indices) {
            if (grid[row][col]
                // Second tree row
                && grid.safeGet(row + 1, col - 1)
                && grid.safeGet(row + 1, col)
                && grid.safeGet(row + 1, col + 1)
                // Third tree row
                && grid.safeGet(row + 2, col - 2)
                && grid.safeGet(row + 2, col - 1)
                && grid.safeGet(row + 2, col)
                && grid.safeGet(row + 2, col + 1)
                && grid.safeGet(row + 2, col + 2)
                ) {
                println("Tree at $row, $col")
                treeTop = row to col
                break@tree
            }
        }
    }

    if (print || treeTop != null) {
        println("-----------------------------------------------------------------------------------------")
        // Print line per two rows, to make the grid square
        for (row in grid.indices step 2) {
            for (col in grid[0].indices) {
                val topCell = grid[row][col]
                val bottomCell = grid.getOrNull(row + 1)?.getOrNull(col) ?: false

                if (treeTop != null && (treeTop == row to col || treeTop == row + 1 to col)) {
                    print(ANSI_RED)
                }

                if (topCell && bottomCell) {
                    print(FULL_BLOCK_CHAR)
                } else if (topCell) {
                    print(TOP_HALF_BLOCK_CHAR)
                } else if (bottomCell) {
                    print(BOTTOM_HALF_BLOCK_CHAR)
                } else {
                    print(" ")
                }

                if (treeTop != null && (treeTop == row to col || treeTop == row + 1 to col)) {
                    print(ANSI_RESET)
                }
            }
            println()
        }
    }

    return treeTop != null
}

fun Array<Array<Boolean>>.safeGet(row: Int, col: Int): Boolean {
    if (row < 0 || row >= GRID_HEIGHT || col < 0 || col >= GRID_WIDTH) {
        return false
    }

    return this[row][col]
}
