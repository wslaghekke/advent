package year2024.day12

import getInputResourceLines

data class Position(
    val row: Int,
    val col: Int
)

data class GardenPlot(
    val symbol: Char,
    val positions: MutableSet<Position>
) {
    val area: Int get() = positions.size
    var perimeter: Int = 0
        private set
    var sides: Int = 0
        private set
    val price: Int get() = perimeter * area

    fun calculatePerimeter(grid: Array<Array<GardenPlot?>>) {
        this.perimeter = positions.sumOf { position ->
            val adjacentPositions = setOf(
                Position(position.row - 1, position.col),
                Position(position.row + 1, position.col),
                Position(position.row, position.col - 1),
                Position(position.row, position.col + 1)
            )
            // Positions contribute to perimeter if they are a plot but not part of this plot
            adjacentPositions.count {
                val adjacentPlot = grid[it]
                adjacentPlot == null || adjacentPlot != this
            }
        }
    }

    fun calculateSides(grid: Array<Array<GardenPlot?>>) {
        sides = 0
        // First loop over grid per row
        var top = false
        var bottom = false
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col] == this) {
                    // If plot is current plot be may potentially have a side
                    val topPlot = grid[Position(row - 1, col)]
                    val bottomPlot = grid[Position(row + 1, col)]
                    if (topPlot == null || topPlot != this) {
                        top = true
                    } else {
                        if (top) {
                            sides++
                            top = false
                        }
                    }
                    if (bottomPlot == null || bottomPlot != this) {
                        bottom = true
                    } else {
                        if (bottom) {
                            sides++
                            bottom = false
                        }
                    }
                } else {
                    // Otherwise we check if sides were found and reset the flags
                    if (top) {
                        sides++
                        top = false
                    }
                    if (bottom) {
                        sides++
                        bottom = false
                    }
                }
            }
            if (top) {
                sides++
                top = false
            }
            if (bottom) {
                sides++
                bottom = false
            }
        }
        // Second loop over grid per column
        var left = false
        var right = false
        for (col in grid[0].indices) {
            for (row in grid.indices) {
                if (grid[row][col] == this) {
                    // If plot is current plot be may potentially have a side
                    val leftPlot = grid[Position(row, col - 1)]
                    val rightPlot = grid[Position(row, col + 1)]
                    if (leftPlot == null || leftPlot != this) {
                        left = true
                    } else {
                        if (left) {
                            sides++
                            left = false
                        }
                    }
                    if (rightPlot == null || rightPlot != this) {
                        right = true
                    } else {
                        if (right) {
                            sides++
                            right = false
                        }
                    }
                } else {
                    // Otherwise we check if sides were found and reset the flags
                    if (left) {
                        sides++
                        left = false
                    }
                    if (right) {
                        sides++
                        right = false
                    }
                }
            }
            if (left) {
                sides++
                left = false
            }
            if (right) {
                sides++
                right = false
            }
        }
    }

    override fun toString(): String {
        return "GardenPlot(symbol=$symbol, area=$area, perimeter=$perimeter, price=$price, sides=$sides)"
    }
}

fun main() {
    val inputRaw = getInputResourceLines(2024, 12)
    val grid = inputRaw.map { size -> arrayOfNulls<GardenPlot>(size.length) }.toTypedArray()
    val gardenPlots = mutableListOf<GardenPlot>()

    var lastGardenPlot: GardenPlot? = null

    // Build garden plots
    // First pass detects horizontal matching symbols and groups them into garden plots
    for (row in inputRaw.indices) {
        for (col in inputRaw[row].indices) {
            val symbol = inputRaw[row][col]
            val position = Position(row, col)
            if (lastGardenPlot?.symbol == symbol) {
                // Add position to last garden plot
                lastGardenPlot.positions.add(position)
            } else {
                // Create new garden plot
                lastGardenPlot = GardenPlot(symbol, mutableSetOf(position))
                gardenPlots.add(lastGardenPlot)
            }
            grid[position] = lastGardenPlot
        }
        // End of row, reset last garden plot
        lastGardenPlot = null
    }

    // Second pass, iterate over garden plots and merge them if any of their positions are adjacent and have the same symbol
    for (row in grid.indices) {
        for (col in grid[row].indices) {
            val position = Position(row, col)
            val gardenPlot = grid[position]!!
            val gardenPlotBelow = grid[Position(row + 1, col)]
            if (gardenPlotBelow != null && gardenPlotBelow != gardenPlot && gardenPlotBelow.symbol == gardenPlot.symbol) {
                // Merge garden plots
                gardenPlot.positions.addAll(gardenPlotBelow.positions)
                if (!gardenPlots.remove(gardenPlotBelow)) {
                    throw IllegalStateException("Failed to remove garden plot")
                }
                for (plotPosition in gardenPlotBelow.positions) {
                    grid[plotPosition] = gardenPlot
                }
            }
        }
    }

    // Calculate perimeters
    for (gardenPlot in gardenPlots) {
        gardenPlot.calculatePerimeter(grid)
        gardenPlot.calculateSides(grid)
    }

    val totalPrice = gardenPlots.sumOf { it.price }

    println("Total price: $totalPrice")

    val newTotalPrice = gardenPlots.sumOf { it.area * it.sides }

    println("New total price: $newTotalPrice")
}

operator fun <T> Array<Array<T>>.set(position: Position, item: T) {
    this[position.row][position.col] = item
}

operator fun <T: Any?> Array<Array<T>>.get(position: Position): T? {
    return if (position.row in this.indices && position.col in this[position.row].indices) {
        this[position.row][position.col]
    } else {
        null
    }
}
