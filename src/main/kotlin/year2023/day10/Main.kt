package year2023.day10

import getInputResourceLines
import java.util.LinkedList

fun main() {
    val lines = getInputResourceLines(2023, 10)
    val grid = PipeGrid(lines.map { it.toList().toTypedArray() }.toTypedArray())

    println("Largest dist: ${grid.findLargestDistance()}")

    println("Total enclosed tiles: ${grid.getEnclosedTileCount()}")
}

class PipeGrid(
    val grid: Array<Array<Char>>
) {
    private var distanceGrid: Array<Array<Int>> = Array(grid.size) { Array(grid[0].size) { -1 } }

    data class Step(
        val x: Int,
        val y: Int,
        val distance: Int
    )

    private fun findStart(): Step {
        for ((rowIndex, row) in grid.withIndex()) {
            for ((index, char) in row.withIndex()) {
                if (char == 'S') {
                    return Step(rowIndex, index, 0)
                }
            }
        }
        throw RuntimeException("No start found");
    }

    fun findLargestDistance(): Int {
        val start = findStart()
        distanceGrid[start.x][start.y] = 0
        val stepQueue = LinkedList(listOf(start))

        while (stepQueue.isNotEmpty()) {
            val step = stepQueue.pollFirst()
            for ((possibleX, possibleY) in possibleDirections(step.x, step.y)) {
                val currDirDistance = distanceGrid[possibleX][possibleY]
                val possibleStepDistance = step.distance + 1
                // -1 means cell is unset so always consider "shorter" dist
                if (currDirDistance == -1 || possibleStepDistance < currDirDistance) {
                    // We found a new short path
                    distanceGrid[possibleX][possibleY] = possibleStepDistance
                    // Add step to queue for checking
                    stepQueue.addLast(Step(possibleX, possibleY, possibleStepDistance))
                }
            }
        }

        for (row in distanceGrid) {
            println(row.map { if (it == -1) '.' else 'X' }.joinToString(""))
        }

        return distanceGrid.maxOf { row -> row.max() }
    }

    fun possibleDirections(x: Int, y: Int) = sequence<Pair<Int, Int>> {
        when (val type = grid[x][y]) {
            '|' -> {
                yield(x - 1 to y)
                yield(x + 1 to y)
            }

            '-' -> {
                yield(x to y - 1)
                yield(x to y + 1)
            }

            'L' -> {
                yield(x - 1 to y)
                yield(x to y + 1)
            }

            'J' -> {
                yield(x - 1 to y)
                yield(x to y - 1)
            }

            '7' -> {
                yield(x to y - 1)
                yield(x + 1 to y)
            }

            'F' -> {
                yield(x to y + 1)
                yield(x + 1 to y)
            }

            'S' -> {
                // Special case, find all symbols pointing to this
                if (y >= 1) {
                    val left = grid[x][y - 1]
                    if (left == 'F' || left == '-' || left == 'L') {
                        yield(x to y - 1)
                    }
                }
                if ((y + 1) < grid[x].size) {
                    val right = grid[x][y + 1]
                    if (right == 'J' || right == '-' || right == '7') {
                        yield(x to y + 1)
                    }
                }
                if (x >= 1) {
                    val top = grid[x - 1][y]
                    if (top == 'F' || top == '|' || top == '7') {
                        yield(x - 1 to y)
                    }
                }
                if ((x + 1) < grid.size) {
                    val bottom = grid[x + 1][y]
                    if (bottom == 'L' || bottom == '|' || bottom == 'J') {
                        yield(x + 1 to y)
                    }
                }
            }

            else -> {
                println("Encountered invalid pipe '$type'")
            }
        }
    }

    fun getEnclosedTileCount(): Int {
        val clonedGrid = grid.map { it.clone() }
        for ((x, row) in clonedGrid.withIndex()) {
            for ((y, char) in row.withIndex()) {
                if (char == 'S') {
                    var leftCon = false;
                    var rightCon = false;
                    var topCon = false;
                    var bottomCon = false;

                    // Special case, find all symbols pointing to this
                    if (y >= 1) {
                        val left = grid[x][y - 1]
                        if (left == 'F' || left == '-' || left == 'L') {
                            leftCon = true
                        }
                    }
                    if ((y + 1) < grid[x].size) {
                        val right = grid[x][y + 1]
                        if (right == 'J' || right == '-' || right == '7') {
                            rightCon = true
                        }
                    }
                    if (x >= 1) {
                        val top = grid[x - 1][y]
                        if (top == 'F' || top == '|' || top == '7') {
                            topCon = true;
                        }
                    }
                    if ((x + 1) < grid.size) {
                        val bottom = grid[x + 1][y]
                        if (bottom == 'L' || bottom == '|' || bottom == 'J') {
                            bottomCon = true;
                        }
                    }

                    clonedGrid[x][y] = when {
                        leftCon && rightCon -> '-'
                        topCon && bottomCon -> '|'
                        topCon && rightCon -> 'L'
                        topCon && leftCon -> 'J'
                        bottomCon && leftCon -> '7'
                        rightCon && bottomCon -> 'F'
                        else -> throw RuntimeException("Invalid connections")
                    }
                }
            }
        }

        var insideLoop = false
        var pipeEnterChar: Char? = null
        for ((x, row) in clonedGrid.withIndex()) {
            for ((y, char) in row.withIndex()) {
                if (char == '.' || distanceGrid[x][y] == -1) {
                    // Chars nog part of loop count as '.'
                    clonedGrid[x][y] = if (insideLoop) 'I' else 'O'
                } else if (char == '|') {
                    insideLoop = !insideLoop
                } else if (char == 'L' || char == 'J' || char == '7' || char == 'F') {
                    if (pipeEnterChar == null) {
                        pipeEnterChar = char
                    } else {
                        // F--7: telt niet
                        // L--J: telt niet

                        if ((pipeEnterChar == 'F' && char == 'J') || (pipeEnterChar == 'L' && char == '7')) {
                            insideLoop = !insideLoop
                        }
                        pipeEnterChar = null
                    }
                }
            }
        }

        for (row in clonedGrid) {
            println(row.joinToString(""))
        }

        return clonedGrid.sumOf { row -> row.count { char -> char == 'I' } }
    }
}
