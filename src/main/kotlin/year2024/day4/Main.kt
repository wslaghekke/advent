package year2024.day4

import getInputResourceLines

fun main() {
    val inputRaw = getInputResourceLines(2024, 4)

    val grid = inputRaw.toTypedArray()
    val word = "XMAS";
    val wordReversed = word.reversed();
    val wordLength = word.length;
    val wordOccurrences = grid.asSequence().flatMapIndexed { rowIndex: Int, row: String ->
        row.asSequence().flatMapIndexed { colIndex: Int, _: Char ->
            sequence<String> {
                // Yield row if word fits
                if (colIndex + wordLength <= grid.size) {
                    yield(row.substring(colIndex, colIndex + wordLength))
                }
                // Yield column if word fits
                if (rowIndex + wordLength <= row.length) {
                    yield((rowIndex..<rowIndex + wordLength).map { grid[it][colIndex] }.joinToString(""))
                }
                // Yield diagonal if word fits
                if (rowIndex + wordLength <= grid.size && colIndex + wordLength <= row.length) {
                    yield((0..<wordLength).map { grid[rowIndex + it][colIndex + it] }.joinToString(""))
                }
                // Yield reverse diagonal if word fits
                if (rowIndex + 1 - wordLength >= 0 && colIndex + wordLength <= row.length) {
                    yield((0..<wordLength).map { grid[rowIndex - it][colIndex + it] }.joinToString(""))
                }
            }
        }
    }.count { it == word || it == wordReversed }

    println("Part 1: Word occurrences: $wordOccurrences")

    var xmasCount = 0;

    for (rowI in grid.indices) {
        for (colI in grid[rowI].indices) {
            if (grid[rowI][colI] == 'A') {
                // A found, start checking surroundings
                val topLeft = grid.safeGet(rowI - 1, colI - 1)
                val topRight = grid.safeGet(rowI - 1, colI + 1)
                val bottomLeft = grid.safeGet(rowI + 1, colI - 1)
                val bottomRight = grid.safeGet(rowI + 1, colI + 1)

                // Check first MAS
                if (topLeft == 'M' && bottomRight == 'S' || topLeft == 'S' && bottomRight == 'M') {
                    // Check second MAS
                    if (topRight == 'M' && bottomLeft == 'S' || topRight == 'S' && bottomLeft == 'M') {
                        xmasCount++;
                    }
                }
            }
        }
    }

    println("Part 2: XMAS count: $xmasCount")
}

fun Array<String>.safeGet(row: Int, col: Int): Char? {
    if (row < 0 || row >= this.size || col < 0 || col >= this[0].length) {
        return null;
    }
    return this[row][col]
}
