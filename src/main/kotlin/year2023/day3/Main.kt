package year2023.day3

import getInputResourceLines

fun main() {
    val schematic = EngineSchematic(getInputResourceLines(2023, 3))
    val partNumbersSum = schematic.grid.flatMap { row ->
        row
            .filterIsInstance<EngineSchematic.GridSymbol>()
            .flatMap { gridSymbol -> gridSymbol.findAdjacentNumbers() }
    }.toSet().sumOf { number -> number.value }
    println("Sum of part numbers: $partNumbersSum")
    val gearRatioSum = schematic.grid.flatMap { row ->
        row
            .filterIsInstance<EngineSchematic.GridSymbol>()
            .filter { it.symbol == '*' }
            .mapNotNull {
                val adjacentNumbers = it.findAdjacentNumbers()
                if (adjacentNumbers.size == 2) {
                    adjacentNumbers.first().value * adjacentNumbers.last().value
                } else null
            }
    }.sum()
    println("Sum of gear ratios: $gearRatioSum")
}

class EngineSchematic(
    inputLines: List<String>
) {
    val grid: List<List<GridItem?>> = inputLines.mapIndexed { x, line ->
        var currNumber = ""
        var currNumberCoords = mutableListOf<Pair<Int, Int>>()
        val gridLineItems: MutableList<GridItem?> = line.map { null }.toMutableList()

        val completeCurrNumber = completeCurrNumber@{
            if (currNumber == "") return@completeCurrNumber
            val gridNumber = GridNumber(currNumber.toInt())
            currNumberCoords.forEach { (_, coordY) ->
                gridLineItems[coordY] = gridNumber
            }
            currNumber = ""
            currNumberCoords = mutableListOf()
        }

        line.forEachIndexed { y, char ->
            if (char.isDigit()) {
                currNumber += char
                currNumberCoords += x to y
            } else {
                completeCurrNumber()
                if (char != '.') {
                    gridLineItems[y] = GridSymbol(char, x, y)
                }
            }
        }
        // Finish trailing number
        completeCurrNumber()

        gridLineItems
    }

    fun getItemOrGridEmpty(x: Int, y: Int): GridItem? = if (x < 0 || y < 0 || x >= grid.size || y >= grid[x].size) { null } else { grid[x][y] }

    sealed interface GridItem
    inner class GridSymbol(
        val symbol: Char,
        private val x: Int,
        private val y: Int,
    ) : GridItem {
        fun findAdjacentNumbers(): Set<GridNumber> = sequence {
            yield(x - 1 to y - 1)
            yield(x - 1 to y)
            yield(x - 1 to y + 1)
            yield(x to y - 1)
            // Don't yield x,y
            yield(x to y + 1)
            yield(x + 1 to y - 1)
            yield(x + 1 to y)
            yield(x + 1 to y + 1)
        }.mapNotNull { getItemOrGridEmpty(it.first, it.second) as? GridNumber }.toSet()
    }
    inner class GridNumber(
        val value: Int
    ) : GridItem
}
