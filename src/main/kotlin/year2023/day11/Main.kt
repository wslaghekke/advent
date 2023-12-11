package year2023.day11

import getInputResourceLines
import kotlin.math.abs

fun main() {
    val lines = getInputResourceLines(2023, 11)

    val grid = lines.map { line -> line.toList() }
    val rowsWithoutGalaxies = grid.indices.filter { rowIndex ->
        grid[rowIndex].none { it == '#' }
    }
    val columnsWithoutGalaxies = grid[0].indices.filter { columnIndex ->
        grid.none { row -> row[columnIndex] == '#' }
    }

    var nextGalaxyIndex = 1;
    val galaxyCoordinates = grid.flatMapIndexed { rowIndex: Int, row: List<Char> ->
        row.mapIndexedNotNull { columnIndex: Int, char: Char ->
            if (char == '#') {
                Galaxy(rowIndex, columnIndex, nextGalaxyIndex++)
            } else {
                null
            }
        }
    }

    println()
    // Print grid with empty rows filled with 'o'
    for ((rowIndex, row) in grid.withIndex()) {
        println(row.mapIndexed { colIndex, char ->
            if (char == '#') {
                galaxyCoordinates.find { galaxy -> galaxy.row == rowIndex && galaxy.column == colIndex }!!.index
            } else {
                if (rowIndex in rowsWithoutGalaxies || colIndex in columnsWithoutGalaxies) {
                    'o'
                } else {
                    '.'
                }
            }
        }.joinToString(""))
    }
    println()

    // Make sequence of all combinations of galaxies
    val galaxyCombinations = sequence {
        for ((index, firstGalaxy) in galaxyCoordinates.withIndex()) {
            for (secondGalaxy in galaxyCoordinates.drop(index + 1)) {
                yield(Pair(firstGalaxy, secondGalaxy))
            }
        }
    }

    fun calculateDistance(firstGalaxy: Galaxy, secondGalaxy: Galaxy, galaxyExpansion: Int): Long {
        val rowRange = absRange(firstGalaxy.row, secondGalaxy.row)
        val columnRange = absRange(firstGalaxy.column, secondGalaxy.column)

        val rowCount = abs(firstGalaxy.row - secondGalaxy.row)
        val colCount = abs(firstGalaxy.column - secondGalaxy.column)
        val emptyRowCount = rowsWithoutGalaxies.count { it in rowRange }
        val emptyColumnCount = columnsWithoutGalaxies.count { it in columnRange }
        // We already counted the empty rows in the 'normal' count, so we subtract them here
        val emptyExpansion = galaxyExpansion - 1

        val dist = rowCount + colCount + (emptyRowCount * emptyExpansion) + (emptyColumnCount * emptyExpansion)
//        println("Distance between $firstGalaxy and $secondGalaxy: " +
//                "$dist, row count: $rowCount, col count: $colCount, empty row count: $emptyRowCount, empty col count: $emptyColumnCount")

        return dist.toLong()
    }

    val sumOfDistances = galaxyCombinations.sumOf { (firstGalaxy, secondGalaxy) ->
        calculateDistance(firstGalaxy, secondGalaxy, 1)
    }

    println("Sum of distances: $sumOfDistances")


    val sumOfDistances2 = galaxyCombinations.sumOf { (firstGalaxy, secondGalaxy) ->
        calculateDistance(firstGalaxy, secondGalaxy, 1000000)
    }

    println("Sum of distances with more galaxy expansion: $sumOfDistances2")

}

fun absRange(a: Int, b: Int) = if (a < b) {
    a..b
} else {
    b..a
}

data class Galaxy(
    val row: Int,
    val column: Int,
    val index: Int
) {
    override fun toString(): String = "#$index ($row,$column)"
}


