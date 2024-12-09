package year2024.day8

import getInputResourceLines

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

data class Antenna(
    val char: Char,
    val position: Position
) {
    operator fun compareTo(other: Antenna): Int {
        return if (position.row == other.position.row) {
            position.col.compareTo(other.position.col)
        } else {
            position.row.compareTo(other.position.row)
        }
    }
}

fun main() {
    val inputRaw = getInputResourceLines(2024, 8)

    val grid = inputRaw.map { it.toCharArray() }.toTypedArray()

    val antiNodes = mutableSetOf<Position>()
    val antiNodesIncludingResonant = mutableSetOf<Position>()

    val antennas: List<Antenna> = grid.flatMapIndexed { rowIndex, row ->
        row.asSequence().mapIndexedNotNull { colIndex, char ->
            if (char != '.') {
                Antenna(char, Position(rowIndex, colIndex))
            } else {
                null
            }
        }
    }

    for (antenna in antennas) {
        for (antenna2 in antennas) {
            // If antennas have the same character and are not in the same position
            if (antenna.position != antenna2.position && antenna.char == antenna2.char) {
                val rowDiff = antenna.position.row - antenna2.position.row
                val colDiff = antenna.position.col - antenna2.position.col

                var a = Position(antenna2.position.row - rowDiff, antenna2.position.col - colDiff)
                var b = Position(antenna.position.row + rowDiff, antenna.position.col + colDiff)

                antiNodesIncludingResonant.add(antenna.position)
                antiNodesIncludingResonant.add(antenna2.position)

                var firstA = true
                while (a.isValid(grid)) {
                    if (firstA) {
                        antiNodes.add(a)
                        firstA = false
                    }

                    antiNodesIncludingResonant.add(a)
                    a = Position(a.row - rowDiff, a.col - colDiff)
                }

                var firstB = true
                while (b.isValid(grid)) {
                    if (firstB) {
                        antiNodes.add(b)
                        firstB = false
                    }

                    antiNodesIncludingResonant.add(b)
                    b = Position(b.row + rowDiff, b.col + colDiff)
                }

                //printGrid(grid, antinodes)
            }
        }
    }

    printGrid(grid, antiNodes)

    println("Part 1: ${antiNodes.size} unique anti-node positions")

    printGrid(grid, antiNodesIncludingResonant)

    println("Part 2: ${antiNodesIncludingResonant.size} unique anti-node positions including resonant")

}

fun printGrid(grid: Array<CharArray>, antiNodes: Set<Position>) {
    for (row in grid.indices) {
        for (col in grid[0].indices) {
            if (Position(row, col) in antiNodes) {
                print('#')
            } else {
                print(grid[row][col])
            }
        }
        println()
    }
    println()
}
